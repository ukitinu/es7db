package ukitinu.es7db;

import ukitinu.es7db.exceptions.DatabaseException;
import ukitinu.es7db.search.Query;
import ukitinu.es7db.search.Search;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

final class ElasticClient
{
    private static final Logger LOG = LoggerFactory.getLogger(ElasticClient.class);

    private final BulkProcessor bulkProcessor;
    private final RestHighLevelClient client;

    private static final ElasticClient INSTANCE = new ElasticClient();

    static synchronized ElasticClient getInstance()
    {
        return INSTANCE;
    }

    private ElasticClient()
    {
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(DatabaseConstants.HOSTNAME, DatabaseConstants.HTTP_PORT, DatabaseConstants.DB_SCHEME)
                )
        );
        bulkProcessor = BulkProcessor.builder(
                (request, bulkListener) -> client.bulkAsync(request, RequestOptions.DEFAULT, bulkListener),
                new BulkProcessor.Listener()
                {
                    @Override
                    public void beforeBulk(long executionId, BulkRequest request)
                    {
                        request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
                        LOG.info(DatabaseConstants.LOG_BULK_ID_ACTIONS, executionId, request.numberOfActions());
                    }

                    @Override
                    public void afterBulk(long executionId, BulkRequest request, BulkResponse response)
                    {
                        LOG.debug(DatabaseConstants.LOG_BULK_EXECUTED, executionId, response.hasFailures() ? DatabaseConstants.LOG_BULK_EXECUTED_FAIL : DatabaseConstants.LOG_BULK_EXECUTED_GOOD);
                    }

                    @Override
                    public void afterBulk(long executionId, BulkRequest request, Throwable failure)
                    {
                        LOG.error(DatabaseConstants.LOG_BULK_ERROR, executionId, failure.getMessage());
                    }
                })
                .setBulkActions(DatabaseConstants.ES_CONFIG_BULK_MAX_SIZE)
                .setBulkSize(new ByteSizeValue(DatabaseConstants.ES_CONFIG_BULK_FLUSH_SIZE, ByteSizeUnit.MB))
                .setFlushInterval(TimeValue.timeValueSeconds(DatabaseConstants.ES_CONFIG_BULK_FLUSH_TIME))
                .setConcurrentRequests(DatabaseConstants.ES_CONFIG_BULK_CONCURRENTS)
                .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(DatabaseConstants.ES_CONFIG_BACKOFF_TIME), DatabaseConstants.ES_CONFIG_BACKOFF_TRIES))
                .build();
    }

    Document get(String index, String id) throws DatabaseException
    {
        try {
            GetRequest request = new GetRequest(index, id);
            GetResponse response = client.get(request, RequestOptions.DEFAULT);
            return response.isExists() ? new Document(response) : null;
        } catch (Exception e) {
            throw DatabaseException.wrapException(e);
        }
    }

    String indexSingle(String index, String id, Map<String, ?> document) throws DatabaseException
    {
        try {
            IndexRequest request = new IndexRequest(index).id(id).source(document);
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            return response.getResult().toString();
        } catch (Exception e) {
            throw DatabaseException.wrapException(e);
        }
    }

    void indexBulk(String index, String id, Map<String, ?> document)
    {
        IndexRequest request = new IndexRequest(index).id(id).source(document);
        bulkProcessor.add(request);
    }

    boolean exists(String index, String id) throws DatabaseException
    {
        try {
            GetRequest request = new GetRequest(index, id);
            request.fetchSourceContext(FetchSourceContext.DO_NOT_FETCH_SOURCE).storedFields(DatabaseConstants.STORED_FIELDS_NONE);
            return client.exists(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw DatabaseException.wrapException(e);
        }
    }

    String deleteSingle(String index, String id) throws DatabaseException
    {
        try {
            DeleteRequest request = new DeleteRequest(index, id);
            DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
            return response.getResult().toString();
        } catch (Exception e) {
            throw DatabaseException.wrapException(e);
        }
    }

    void deleteBulk(String index, String id)
    {
        DeleteRequest request = new DeleteRequest(index, id);
        bulkProcessor.add(request);
    }

    String updateSingle(String index, String id, Map<String, ?> update) throws DatabaseException
    {
        try {
            UpdateRequest request = buildUpdateRequest(index, id, update);
            UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
            return response.getResult().toString();
        } catch (Exception e) {
            throw DatabaseException.wrapException(e);
        }
    }

    void updateBulk(String index, String id, Map<String, ?> update)
    {
        UpdateRequest request = buildUpdateRequest(index, id, update);
        bulkProcessor.add(request);
    }

    long count(String index, Query query) throws DatabaseException
    {
        try {
            CountRequest request = new CountRequest(index).query(query.toQuery());
            CountResponse response = client.count(request, RequestOptions.DEFAULT);
            return response.getCount();
        } catch (Exception e) {
            throw DatabaseException.wrapException(e);
        }
    }

    SearchResult search(Search search) throws DatabaseException
    {
        try {
            SearchRequest request = new SearchRequest(search.getIndex());
            request.source(search.toSearch());
            if (search.isScroll()) {
                return searchScrolled(request);
            } else {
                return searchSized(request);
            }
        } catch (Exception e) {
            throw DatabaseException.wrapException(e);
        }
    }

    BucketAggregationResult aggregateTerm(String index, Query query, String field, int size, List<String> excludeKw) throws DatabaseException
    {
        try {
            AggregationBuilder termAggregation = Aggregations.newTermAggregation(field, size, excludeKw);
            SearchResponse response = aggregate(index, query, termAggregation);

            BucketAggregationResult results = new BucketAggregationResult();

            Terms terms = response.getAggregations().get(field);
            results.setOtherCount(terms.getSumOfOtherDocCounts());
            for (Terms.Bucket bucket : terms.getBuckets()) {
                results.addEntry(bucket.getKeyAsString(), bucket.getDocCount());
            }
            return results;
        } catch (Exception e) {
            throw DatabaseException.wrapException(e);
        }
    }

    BucketAggregationResult getTimeHistogram(String index, Query query, String field, HistogramInterval interval) throws DatabaseException
    {
        try {
            AggregationBuilder dateHistogramAggregation = Aggregations.newDateHistogramAggregation(field, interval);
            SearchResponse response = aggregate(index, query, dateHistogramAggregation);

            BucketAggregationResult results = new BucketAggregationResult();

            Histogram histogram = response.getAggregations().get(field);
            for (Histogram.Bucket bucket : histogram.getBuckets()) {
                results.addEntry(bucket.getKeyAsString(), bucket.getDocCount());
            }
            return results;
        } catch (Exception e) {
            throw DatabaseException.wrapException(e);
        }
    }

    private SearchResponse aggregate(String index, Query query, AggregationBuilder aggregation) throws IOException
    {
        SearchRequest request = new SearchRequest(index);
        Search search = new Search.Builder(index, query).withBounds(0, 0).build();
        request.source(search.toSearch().aggregation(aggregation));
        return client.search(request, RequestOptions.DEFAULT);
    }

    private SearchResult searchSized(SearchRequest request) throws IOException
    {
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        SearchHits hits = response.getHits();
        long total = hits.getTotalHits().value;

        return new SearchResult(hits.getHits(), total);
    }

    private SearchResult searchScrolled(SearchRequest request) throws IOException
    {
        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));

        request.scroll(scroll);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        List<Document> documents = new ArrayList<>();
        long total = response.getHits().getTotalHits().value;

        String scrollId = response.getScrollId();
        SearchHit[] searchHits = response.getHits().getHits();

        while (searchHits != null && searchHits.length > 0) {
            //save results
            documents.addAll(Arrays
                    .stream(searchHits)
                    .map(Document::new)
                    .collect(Collectors.toList())
            );
            //next page
            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
            scrollRequest.scroll(scroll);
            response = client.scroll(scrollRequest, RequestOptions.DEFAULT);
            //new data
            scrollId = response.getScrollId();
            searchHits = response.getHits().getHits();
        }
        //clean scroll
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);

        return new SearchResult(documents, total);
    }

    private UpdateRequest buildUpdateRequest(String index, String id, Map<String, ?> update)
    {
        UpdateRequest request = new UpdateRequest(index, id).doc(update);
        request.docAsUpsert(true);
        request.retryOnConflict(DatabaseConstants.RETRY_ON_CONFLICT_TIMES);
        request.fetchSource(false);
        return request;
    }
}

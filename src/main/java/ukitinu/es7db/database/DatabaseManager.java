package ukitinu.es7db.database;

import ukitinu.es7db.database.exceptions.DatabaseException;
import ukitinu.es7db.database.search.Query;
import ukitinu.es7db.database.search.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.http.HttpStatus;

import java.util.List;
import java.util.Map;

public enum DatabaseManager
{
    INSTANCE;

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseManager.class);
    private static final ElasticClient CLIENT = ElasticClient.getInstance();

    public Document get(String index, String id) throws DatabaseException
    {
        Document document = CLIENT.get(index, id);
        if (document == null) throw new DatabaseException(index + "/" + id + " not found", HttpStatus.SC_NOT_FOUND);
        return document;
    }

    public Document pop(String index, String id) throws DatabaseException
    {
        Document document = CLIENT.get(index, id);
        if (document == null) throw new DatabaseException(index + "/" + id + " not found", HttpStatus.SC_NOT_FOUND);
        deleteSingle(index, id);
        return document;
    }

    public void indexSingle(String index, String id, Map<String, ?> document) throws DatabaseException
    {
        String response = CLIENT.indexSingle(index, id, document);
        LOG.info(response);
    }

    public void indexBulk(String index, String id, Map<String, ?> document)
    {
        CLIENT.indexBulk(index, id, document);
    }

    public boolean exists(String index, String id) throws DatabaseException
    {
        return CLIENT.exists(index, id);
    }

    public void deleteSingle(String index, String id) throws DatabaseException
    {
        String response = CLIENT.deleteSingle(index, id);
        LOG.info(response);
    }

    public void deleteBulk(String index, String id)
    {
        CLIENT.deleteBulk(index, id);
    }

    public void updateSingle(String index, String id, Map<String, ?> document) throws DatabaseException
    {
        String response = CLIENT.updateSingle(index, id, document);
        LOG.info(response);
    }

    public void updateBulk(String index, String id, Map<String, ?> document)
    {
        CLIENT.updateBulk(index, id, document);
    }

    public SearchResult search(Search search) throws DatabaseException
    {
        return CLIENT.search(search);
    }

    public long count(String index, Query query) throws DatabaseException
    {
        return CLIENT.count(index, query);
    }

    public boolean exists(String index, Query query) throws DatabaseException
    {
        return count(index, query) > 0;
    }

    public Document getFirstDocument(Search search) throws DatabaseException
    {
        SearchResult result = search(search);
        if (result.getDocuments().isEmpty()) throw new DatabaseException("Document not found", HttpStatus.SC_NOT_FOUND);
        return result.getDocuments().get(0);
    }

    public BucketAggregationResult aggregateTerm(String index, Query query, String field, int size, List<String> excludeKw) throws DatabaseException
    {
        return CLIENT.aggregateTerm(index, query, field, size, excludeKw);
    }

    public BucketAggregationResult getTimeHistogram(String index, Query query, String field, HistogramInterval interval) throws DatabaseException
    {
        return CLIENT.getTimeHistogram(index, query, field, interval);
    }
}

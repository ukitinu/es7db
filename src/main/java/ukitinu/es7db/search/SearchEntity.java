package ukitinu.es7db.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import ukitinu.es7db.config.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchEntity {
    private static final String JSON_SIZE = "size";
    private static final String JSON_FROM = "from";
    private static final String JSON_SORT = "sort";
    private static final String JSON_FILTERS = "filters";
    private static final String JSON_MUST = "must";
    private static final String JSON_MUST_NOT = "must_not";
    private static final String JSON_SHOULD = "should";
    private static final String JSON_SHOULD_NOT = "should_not";
    private static final String JSON_MIN_SHOULD = "minimum_should";

    @JsonProperty(JSON_SIZE)
    private Integer size;
    @JsonProperty(JSON_FROM)
    private Integer from;
    @JsonProperty(JSON_MIN_SHOULD)
    private Integer minShould;
    @JsonProperty(JSON_SORT)
    private SortElement sort;
    @JsonProperty(JSON_FILTERS)
    private List<Map<String, Object>> filters;
    @JsonProperty(JSON_MUST)
    private List<Map<String, Object>> must;
    @JsonProperty(JSON_MUST_NOT)
    private List<Map<String, Object>> mustNot;
    @JsonProperty(JSON_SHOULD)
    private List<Map<String, Object>> should;
    @JsonProperty(JSON_SHOULD_NOT)
    private List<Map<String, Object>> shouldNot;

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getMinShould() {
        return minShould;
    }

    public void setMinShould(Integer minShould) {
        this.minShould = minShould;
    }

    public SortElement getSort() {
        return sort;
    }

    public void setSort(SortElement sort) {
        this.sort = sort;
    }

    public List<Map<String, Object>> getFilters() {
        return filters;
    }

    public void setFilters(List<Map<String, Object>> filters) {
        this.filters = filters;
    }

    public List<Map<String, Object>> getMust() {
        return must;
    }

    public void setMust(List<Map<String, Object>> must) {
        this.must = must;
    }

    public List<Map<String, Object>> getMustNot() {
        return mustNot;
    }

    public void setMustNot(List<Map<String, Object>> mustNot) {
        this.mustNot = mustNot;
    }

    public List<Map<String, Object>> getShould() {
        return should;
    }

    public void setShould(List<Map<String, Object>> should) {
        this.should = should;
    }

    public List<Map<String, Object>> getShouldNot() {
        return shouldNot;
    }

    public void setShouldNot(List<Map<String, Object>> shouldNot) {
        this.shouldNot = shouldNot;
    }

    public Query buildQuery() {
        Query query = new Query();
        QueryUtils.addFilters(query, filters != null ? filters : new ArrayList<>());
        QueryUtils.addMust(query, must != null ? must : new ArrayList<>());
        QueryUtils.addMustNot(query, mustNot != null ? mustNot : new ArrayList<>());
        QueryUtils.addShould(query, should != null ? should : new ArrayList<>());
        QueryUtils.addShouldNot(query, shouldNot != null ? shouldNot : new ArrayList<>());
        if (query.hasShoulds()) {
            query.minimumShould(minShould != null ? minShould : Property.ES_SEARCH_MIN_SHOULD.getInt());
        }
        return query;
    }

    public Search toSearch(String index) {
        Query query = buildQuery();
        Search.Builder builder = new Search.Builder(index, query);
        if (size != null && size == -1) {
            builder.scroll();
        } else {
            int f = from != null ? from : Property.ES_SEARCH_FROM.getInt();
            int s = size != null ? size : Property.ES_SEARCH_SIZE.getInt();
            builder.withBounds(f, s);
        }
        if (sort != null) {
            builder.sortBy(sort);
        }
        return builder.build();
    }


}

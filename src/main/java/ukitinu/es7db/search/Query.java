package ukitinu.es7db.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import ukitinu.es7db.config.Property;

import java.util.function.Consumer;

public class Query implements Queryable {
    private static final int MINIMUM_SHOULD_DEFAULT = Property.ES_SEARCH_MIN_SHOULD.getInt();
    private static final int MINIMUM_SHOULD_UNDEFINED = 0;

    private static final Logger LOG = LogManager.getLogger(Query.class);

    private final FilterElements filterList = new FilterElements();
    private final MustElements mustList = new MustElements();
    private final FilterElements mustNotList = new FilterElements();
    private final FilterElements shouldList = new FilterElements();
    private final FilterElements shouldNotList = new FilterElements();
    private int minimumShould = MINIMUM_SHOULD_UNDEFINED;

    FilterElements getFilter() {
        return filterList;
    }

    MustElements getMust() {
        return mustList;
    }

    FilterElements getMustNot() {
        return mustNotList;
    }

    FilterElements getShouldList() {
        return shouldList;
    }

    FilterElements getShouldNotList() {
        return shouldNotList;
    }

    public boolean hasShoulds() {
        return !shouldList.getElements().isEmpty() || !shouldNotList.getElements().isEmpty();
    }

    //region filters
    public Query filterTerm(String field, Object... values) {
        filterList.addTerm(field, values);
        return this;
    }

    public Query filterRange(String field, Object value, RangeParameter parameter) {
        filterList.addRange(field, value, parameter);
        return this;
    }

    public Query filterExists(String field) {
        filterList.addExists(field);
        return this;
    }

    public Query filterGeoBox(String field, CoordinatePoint topLeft, CoordinatePoint bottomRight) {
        filterList.addGeoBox(field, topLeft, bottomRight);
        return this;
    }

    public Query filterGeoCircle(String field, CoordinatePoint centre, Distance distance) {
        filterList.addGeoCircle(field, centre, distance);
        return this;
    }

    public Query filterGeoPolygon(String field, CoordinatePoint p1, CoordinatePoint p2, CoordinatePoint p3, CoordinatePoint... points) {
        filterList.addGeoPolygon(field, p1, p2, p3, points);
        return this;
    }
    //endregion

    //region must
    public Query mustMatch(String field, String text, boolean isFuzzy) {
        mustList.addMatch(field, text, isFuzzy);
        return this;
    }
    //endregion

    //region must_not
    public Query mustNotTerm(String field, Object... values) {
        mustNotList.addTerm(field, values);
        return this;
    }

    public Query mustNotRange(String field, Object value, RangeParameter parameter) {
        mustNotList.addRange(field, value, parameter);
        return this;
    }

    public Query mustNotExists(String field) {
        mustNotList.addExists(field);
        return this;
    }

    public Query mustNotGeoBox(String field, CoordinatePoint topLeft, CoordinatePoint bottomRight) {
        mustNotList.addGeoBox(field, topLeft, bottomRight);
        return this;
    }

    public Query mustNotGeoCircle(String field, CoordinatePoint centre, Distance distance) {
        mustNotList.addGeoCircle(field, centre, distance);
        return this;
    }

    public Query mustNotGeoPolygon(String field, CoordinatePoint p1, CoordinatePoint p2, CoordinatePoint p3, CoordinatePoint... points) {
        mustNotList.addGeoPolygon(field, p1, p2, p3, points);
        return this;
    }
    //endregion

    //region should
    public Query minimumShould(int minimumShould) {
        this.minimumShould = minimumShould;
        return this;
    }

    public Query shouldTerm(boolean truth, String field, Object... values) {
        if (truth) {
            shouldList.addTerm(field, values);
        } else {
            shouldNotList.addTerm(field, values);
        }
        return this;
    }

    public Query shouldRange(boolean truth, String field, Object value, RangeParameter parameter) {
        if (truth) {
            shouldList.addRange(field, value, parameter);
        } else {
            shouldNotList.addRange(field, value, parameter);
        }
        return this;
    }

    public Query shouldExists(boolean truth, String field) {
        if (truth) {
            shouldList.addExists(field);
        } else {
            shouldNotList.addExists(field);
        }
        return this;
    }

    public Query shouldGeoBox(boolean truth, String field, CoordinatePoint topLeft, CoordinatePoint bottomRight) {
        if (truth) {
            shouldList.addGeoBox(field, topLeft, bottomRight);
        } else {
            shouldNotList.addGeoBox(field, topLeft, bottomRight);
        }
        return this;
    }

    public Query shouldGeoCircle(boolean truth, String field, CoordinatePoint centre, Distance distance) {
        if (truth) {
            shouldList.addGeoCircle(field, centre, distance);
        } else {
            shouldNotList.addGeoCircle(field, centre, distance);
        }
        return this;
    }

    public Query shouldGeoPolygon(boolean truth, String field, CoordinatePoint p1, CoordinatePoint p2, CoordinatePoint p3, CoordinatePoint... points) {
        if (truth) {
            shouldList.addGeoPolygon(field, p1, p2, p3, points);
        } else {
            shouldNotList.addGeoPolygon(field, p1, p2, p3, points);
        }
        return this;
    }
    //endregion

    @Override
    public QueryBuilder toQuery() {
        if (hasShoulds() && minimumShould == MINIMUM_SHOULD_UNDEFINED) {
            LOG.warn("Should[Not] entries without setting minimum_should, default to {}", MINIMUM_SHOULD_DEFAULT);
            return toQuery(MINIMUM_SHOULD_DEFAULT);
        }
        return toQuery(minimumShould);
    }

    private QueryBuilder toQuery(int minimumShould) {
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        addQueries(filterList.getElements(), queryBuilder::filter);
        addQueries(mustList.getElements(), queryBuilder::must);
        addQueries(mustNotList.getElements(), queryBuilder::mustNot);
        addQueries(shouldList.getElements(), queryBuilder::should);
        addQueries(shouldNotList.getElements(), e -> queryBuilder.should(QueryBuilders.boolQuery().mustNot(e)));
        queryBuilder.minimumShouldMatch(minimumShould);
        return queryBuilder;
    }

    private void addQueries(Iterable<? extends Queryable> list, Consumer<? super QueryBuilder> addQuery) {
        for (Queryable element : list) {
            addQuery.accept(element.toQuery());
        }
    }
}

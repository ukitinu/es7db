package ukitinu.es7db.database.search;

import ukitinu.es7db.commons.Utils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Builds query out of a Map<String, Object> (with JSONs in mind).
 */
public final class QueryUtils
{
    private QueryUtils()
    {
        throw new IllegalStateException("Utils class");
    }

    private static final String TYPE_TERM = "term";
    private static final String TYPE_RANGE = "range";
    private static final String TYPE_EXISTS = "exists";
    private static final String TYPE_GEOBOX = "geobox";

    private static final String FIELD_TYPE = "type";
    private static final String FIELD_KEY = "key";
    private static final String FIELD_VALUE = "value";
    private static final String FIELD_TOP_RIGHT = "top_right";
    private static final String FIELD_BOTTOM_LEFT = "bottom_left";
    private static final String FIELD_LAT = "lat";
    private static final String FIELD_LON = "lon";
    private static final String FIELD_FROM = "from";
    private static final String FIELD_TO = "to";

    private static final int RANGE_FIELD_ABSENT_VALUE = 0;

    public static void addFilters(Query query, Iterable<? extends Map<String, Object>> filterList)
    {
        for (Map<String, Object> filter : filterList) {
            addFilter(query.getFilter(), filter);
        }
    }

    public static void addMust(Query query, Iterable<? extends Map<String, Object>> mustList)
    {
        for (Map<String, Object> must : mustList) {
            addMustMatch(query, must);
        }
    }

    public static void addMustNot(Query query, Iterable<? extends Map<String, Object>> mustNotList)
    {
        for (Map<String, Object> mustNot : mustNotList) {
            addFilter(query.getMustNot(), mustNot);
        }
    }

    public static void addShould(Query query, Iterable<? extends Map<String, Object>> shouldList)
    {
        for (Map<String, Object> mustNot : shouldList) {
            addFilter(query.getShouldList(), mustNot);
        }
    }

    public static void addShouldNot(Query query, Iterable<? extends Map<String, Object>> shouldNotList)
    {
        for (Map<String, Object> mustNot : shouldNotList) {
            addFilter(query.getShouldNotList(), mustNot);
        }
    }

    private static void addMustMatch(Query query, Map<String, Object> match)
    {
        String field = (String) match.get(FIELD_KEY);
        String value = (String) match.get(FIELD_VALUE);
        if (field == null || value == null)
            throw new IllegalArgumentException("Match \"key\" and \"value\" cannot be null");
        query.mustMatch(field, value, true);
    }

    private static void addFilter(FilterElements queryPart, Map<String, Object> filter)
    {
        String type = (String) filter.get(FIELD_TYPE);
        switch (type) {
            case TYPE_TERM:
                addTerm(queryPart, filter);
                break;
            case TYPE_RANGE:
                addRange(queryPart, filter);
                break;
            case TYPE_EXISTS:
                addExists(queryPart, filter);
                break;
            case TYPE_GEOBOX:
                addGeoBox(queryPart, filter);
                break;
            default:
                throw new IllegalArgumentException("Invalid \"type\": " + type);
        }
    }

    private static void addTerm(FilterElements queryPart, Map<String, Object> term)
    {
        String field = (String) term.get(FIELD_KEY);
        if (field == null) throw new IllegalArgumentException("\"key\" cannot be null");

        Object value = term.get(FIELD_VALUE);
        if (value instanceof List) {
            if (((List<?>) value).isEmpty()) {
                throw new IllegalArgumentException("Filter \"key\" " + field + ": empty list in \"value\"");
            } else {
                Object[] list = ((List<?>) value).toArray(new Object[0]);
                queryPart.addTerm(field, list);
            }
        } else {
            queryPart.addTerm(field, value);
        }
    }

    private static void addRange(FilterElements queryPart, Map<String, Object> range)
    {
            String rangeField = (String) range.get(FIELD_KEY);
            if (rangeField == null) throw new IllegalArgumentException("\"key\" cannot be null");

            Map<String, Object> ranges = (Map<String, Object>) range.get(FIELD_VALUE);
            Object from = ranges.get(FIELD_FROM);
            Object to = ranges.get(FIELD_TO);
            if (from == null || to == null)
                throw new IllegalArgumentException(
                        "Range \"from\" and \"to\" cannot be null, use "
                                + RANGE_FIELD_ABSENT_VALUE + " to signal empty value");

            addSingleRange(queryPart, rangeField, from, RangeParameter.GTE);
            addSingleRange(queryPart, rangeField, to, RangeParameter.LTE);
    }

    private static void addSingleRange(FilterElements queryPart, String field, Object value,
                                       RangeParameter parameter)
    {
        if (!Objects.equals(value, RANGE_FIELD_ABSENT_VALUE)) {
            queryPart.addRange(field, value, parameter);
        }
    }

    private static void addExists(FilterElements queryPart, Map<String, Object> exists)
    {
            String field = (String) exists.get(FIELD_KEY);
            if (field == null) throw new IllegalArgumentException("\"key\" cannot be null");

            queryPart.addExists(field);
    }

    private static void addGeoBox(FilterElements queryPart, Map<String, Object> geoBox)
    {
        Map<String, Object> box = (Map<String, Object>) geoBox.get(FIELD_VALUE);
        String geoField = (String) geoBox.get(FIELD_KEY);

        addGeoBox(queryPart, geoField, box);
    }

    private static void addGeoBox(FilterElements queryPart, String geoField, Map<String, Object> box)
    {
            Map<String, Double> topRight = (Map<String, Double>) box.get(FIELD_TOP_RIGHT);
            Map<String, Double> bottomLeft = (Map<String, Double>) box.get(FIELD_BOTTOM_LEFT);

            double topRightLat = Utils.roundCoordinate(topRight.get(FIELD_LAT));
            double topRightLon = Utils.roundCoordinate(topRight.get(FIELD_LON));
            double bottomLeftLat = Utils.roundCoordinate(bottomLeft.get(FIELD_LAT));
            double bottomLeftLon = Utils.roundCoordinate(bottomLeft.get(FIELD_LON));

            CoordinatePoint topLeft = new CoordinatePoint(topRightLat, bottomLeftLon);
            CoordinatePoint bottomRight = new CoordinatePoint(bottomLeftLat, topRightLon);

            if (geoField == null) throw new IllegalArgumentException("Geobox \"key\" cannot be null");
            queryPart.addGeoBox(geoField, topLeft, bottomRight);
    }
}


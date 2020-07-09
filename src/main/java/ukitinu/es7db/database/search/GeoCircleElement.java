package ukitinu.es7db.database.search;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class GeoCircleElement implements GeoQueryable
{
    private final String field;
    private final CoordinatePoint centre;
    private final Distance radius;

    GeoCircleElement(String field, CoordinatePoint centre, Distance radius)
    {
        this.field = field;
        this.centre = centre;
        this.radius = radius;
    }

    @Override
    public QueryBuilder toQuery()
    {
        return QueryBuilders.geoDistanceQuery(field).point(centre.toGeoPoint()).distance(radius.toString());
    }
}

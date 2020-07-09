package ukitinu.es7db.database.search;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class GeoBoxElement implements GeoQueryable
{
    private final String field;
    private final CoordinatePoint topLeft;
    private final CoordinatePoint bottomRight;

    GeoBoxElement(String field, CoordinatePoint topLeft, CoordinatePoint bottomRight)
    {
        this.field = field;
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }

    @Override
    public QueryBuilder toQuery()
    {
        return QueryBuilders.geoBoundingBoxQuery(field).setCorners(topLeft.toGeoPoint(), bottomRight.toGeoPoint());
    }
}

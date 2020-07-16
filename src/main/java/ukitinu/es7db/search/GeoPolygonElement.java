package ukitinu.es7db.search;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GeoPolygonElement implements GeoQueryable
{
    private final String field;
    private final CoordinatePoint[] polygonPoints;

    GeoPolygonElement(String field, CoordinatePoint p1, CoordinatePoint p2, CoordinatePoint p3, CoordinatePoint... points)
    {
        this.field = field;

        int pointsLength = points != null ? points.length : 0;
        CoordinatePoint[] polyPoints = new CoordinatePoint[3 + pointsLength];
        polyPoints[0] = p1;
        polyPoints[1] = p2;
        polyPoints[2] = p3;
        if (points != null) System.arraycopy(points, 0, polyPoints, 3, pointsLength);
        this.polygonPoints = polyPoints;
    }

    @Override
    public QueryBuilder toQuery()
    {
        return QueryBuilders.geoPolygonQuery(
                field,
                Arrays.stream(polygonPoints)
                        .map(CoordinatePoint::toGeoPoint)
                        .collect(Collectors.toList())
        );
    }

}

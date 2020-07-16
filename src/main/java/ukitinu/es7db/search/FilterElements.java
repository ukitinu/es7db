package ukitinu.es7db.search;

import java.util.ArrayList;
import java.util.List;

class FilterElements
{
    private final List<TermElement> terms = new ArrayList<>();
    private final List<RangeElement> ranges = new ArrayList<>();
    private final List<ExistsElement> exists = new ArrayList<>();
    private final List<GeoQueryable> geos = new ArrayList<>();

    List<Queryable> getElements()
    {
        List<Queryable> list = new ArrayList<>(terms);
        list.addAll(new ArrayList<>(ranges));
        list.addAll(new ArrayList<>(exists));
        list.addAll(new ArrayList<>(geos));
        return list;
    }

    void addTerm(String field, Object... values)
    {
        terms.add(new TermElement(field, values));
    }

    void addRange(String field, Object value, RangeParameter parameter)
    {
        ranges.add(new RangeElement(field, value, parameter));
    }

    void addExists(String field)
    {
        exists.add(new ExistsElement(field));
    }

    void addGeoBox(String field, CoordinatePoint topLeft, CoordinatePoint bottomRight)
    {
        geos.add(new GeoBoxElement(field, topLeft, bottomRight));
    }

    void addGeoCircle(String field, CoordinatePoint centre, Distance distance)
    {
        geos.add(new GeoCircleElement(field, centre, distance));
    }

    void addGeoPolygon(String field, CoordinatePoint p1, CoordinatePoint p2, CoordinatePoint p3, CoordinatePoint... points)
    {
        geos.add(new GeoPolygonElement(field, p1, p2, p3, points));
    }
}

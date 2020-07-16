package ukitinu.es7db.search;

import com.fasterxml.jackson.annotation.JsonValue;
import org.elasticsearch.common.geo.GeoPoint;
import ukitinu.es7db.DatabaseUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CoordinatePoint
{
    private static final int COORD_SIZE = 2;
    private static final int LAT_INDEX = 0;
    private static final int LON_INDEX = 1;
    private static final String STRING_SEPARATOR = ", ";
    private static final String ERROR_COORD_NUMBER = "Wrong number of coordinates";
    private static final String ERROR_COORD_FORMAT = "Bad coordinate string. Expected format:\"d.d, d.d\"";

    private final double lat;
    private final double lon;

    public CoordinatePoint(double lat, double lon)
    {
        this.lat = lat;
        this.lon = lon;
    }

    public CoordinatePoint(String coordinates)
    {
        try {
            String[] array = coordinates.split(STRING_SEPARATOR);
            if (array.length != COORD_SIZE) throw new IllegalArgumentException(ERROR_COORD_NUMBER);
            this.lat = DatabaseUtils.roundCoordinate(Double.parseDouble(array[LAT_INDEX]));
            this.lon = DatabaseUtils.roundCoordinate(Double.parseDouble(array[LON_INDEX]));
        } catch (Exception e) {
            throw new IllegalArgumentException(ERROR_COORD_FORMAT);
        }
    }

    public double getLat()
    {
        return lat;
    }

    public double getLon()
    {
        return lon;
    }

    GeoPoint toGeoPoint()
    {
        return new GeoPoint(lat, lon);
    }

    public Map<String, Double> asMap()
    {
        Map<String, Double> object = new HashMap<>();
        object.put("lat", lat);
        object.put("lon", lon);
        return object;
    }

    @Override
    @JsonValue
    public String toString()
    {
        return lat + ", " + lon;
    }

    public double[] toArray()
    {
        return new double[]{lon, lat};
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoordinatePoint that = (CoordinatePoint) o;
        return Double.compare(that.lat, lat) == 0 &&
                Double.compare(that.lon, lon) == 0;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(lat, lon);
    }
}

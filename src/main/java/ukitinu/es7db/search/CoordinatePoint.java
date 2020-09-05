package ukitinu.es7db.search;

import com.fasterxml.jackson.annotation.JsonValue;
import org.elasticsearch.common.geo.GeoPoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoordinatePoint {
    private static final int COORD_SIZE = 2;
    private static final int LAT_INDEX = 0;
    private static final int LON_INDEX = 1;
    private static final String STRING_SEPARATOR = ", ";
    private static final String FMT_DEC = "-?\\d+(?:[.,]\\d+)?";
    private static final Pattern DEG = Pattern.compile(FMT_DEC + "[g°]");
    private static final Pattern MIN = Pattern.compile(FMT_DEC + "'");
    private static final Pattern SEC = Pattern.compile(FMT_DEC + "''");
    private static final int MIN_RATIO = 60;
    private static final int SEC_RATIO = 3600;

    private static final String ERROR_COORD_NUMBER = "Wrong number of coordinates";
    private static final String NULL_PARAM = "Parameter(s) must not be null";
    private static final String EMPTY_PARAM = "Parameter must not be empty";
    private static final String FORMAT_NOT_RECOGNISED = "Coordinate format not recognised: ";

    public static final CoordinatePoint NULL = new CoordinatePoint(0, 0);

    private final double lat;
    private final double lon;

    public CoordinatePoint(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public CoordinatePoint(String coordinates) {
        if (coordinates == null) throw new IllegalArgumentException(NULL_PARAM);
        String[] array = coordinates.split(STRING_SEPARATOR);
        if (array.length != COORD_SIZE) throw new IllegalArgumentException(ERROR_COORD_NUMBER);
        this.lat = parseString(array[LAT_INDEX]);
        this.lon = parseString(array[LON_INDEX]);
    }

    public CoordinatePoint(String lat, String lon) {
        if (lat == null || lon == null) throw new IllegalArgumentException(NULL_PARAM);
        this.lat = parseString(lat);
        this.lon = parseString(lon);
    }

    private double parseString(String string) {
        if (string == null || string.isBlank()) throw new IllegalArgumentException(EMPTY_PARAM);
        if (string.matches(FMT_DEC)) {
            return parseNum(string);
        } else {
            Pattern check = Pattern.compile(FMT_DEC);
            if (!check.matcher(string).find()) throw new IllegalArgumentException(FORMAT_NOT_RECOGNISED + string);

            double value = 0.0;
            value += parsePiece(string, DEG, 1);
            value += parsePiece(string, MIN, 1) / MIN_RATIO;
            value += parsePiece(string, SEC, 2) / SEC_RATIO;
            return value;
        }
    }

    private double parseNum(String string) {
        return Double.parseDouble(string.replaceAll(",", "."));
    }

    private double parsePiece(String string, Pattern pattern, int tail) {
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            String num = string.substring(matcher.start(), matcher.end() - tail);
            return parseNum(num);
        }
        return 0.0;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    GeoPoint toGeoPoint() {
        return new GeoPoint(lat, lon);
    }

    public Map<String, Double> asMap() {
        Map<String, Double> object = new HashMap<>();
        object.put("lat", lat);
        object.put("lon", lon);
        return object;
    }

    @Override
    @JsonValue
    public String toString() {
        return lat + ", " + lon;
    }

    public double[] toArray() {
        return new double[]{lon, lat};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoordinatePoint that = (CoordinatePoint) o;
        return Double.compare(that.lat, lat) == 0 &&
                Double.compare(that.lon, lon) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lon);
    }
}

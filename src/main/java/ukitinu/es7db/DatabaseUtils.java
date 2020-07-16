package ukitinu.es7db;

import ukitinu.es7db.config.Property;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class DatabaseUtils
{
    private DatabaseUtils()
    {
        throw new IllegalStateException("Utility class");
    }

    public static double roundDouble(double dbl, int precision)
    {
        return BigDecimal.valueOf(dbl).setScale(precision, RoundingMode.HALF_UP).doubleValue();
    }

    public static double roundCoordinate(double coordinate)
    {
        int precision = Property.COORDINATE_PRECISION.getInt();
        return roundDouble(coordinate, precision);
    }
}

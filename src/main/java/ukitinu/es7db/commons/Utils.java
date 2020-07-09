package ukitinu.es7db.commons;

import ukitinu.es7db.config.Property;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Utils
{
    private Utils()
    {
        throw new IllegalStateException("Utils class");
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

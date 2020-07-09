package ukitinu.es7db.database.search;

import java.util.Objects;

public class Distance implements Comparable<Distance>
{
    private static final String ERROR_POSITIVE_DISTANCE = "Distance must be positive";

    private final double length;
    private final DistanceUnit unit;

    public Distance(double length, DistanceUnit unit)
    {
        if (length <= 0) throw new IllegalArgumentException(ERROR_POSITIVE_DISTANCE);
        this.length = length;
        this.unit = unit;
    }

    public double convertLength(DistanceUnit newUnit)
    {
        return this.unit == newUnit ? this.length : this.length * this.unit.getMetres() / newUnit.getMetres();
    }

    @Override
    public String toString()
    {
        return length + unit.toString();
    }

    @Override
    public int compareTo(Distance o)
    {
        return Double.compare(this.length, o.convertLength(this.unit));
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance = (Distance) o;
        return Double.compare(distance.length, length) == 0 &&
                unit == distance.unit;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(length, unit);
    }
}

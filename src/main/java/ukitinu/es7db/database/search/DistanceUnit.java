package ukitinu.es7db.database.search;

public enum DistanceUnit
{
    KM("km", 1_000.0D),
    M("m", 1.0D),
    CM("cm", 0.01D);

    private final String unitName;
    private final double metres;

    DistanceUnit(String unitName, double metres)
    {
        this.unitName = unitName;
        this.metres = metres;
    }

    double getMetres()
    {
        return metres;
    }

    @Override
    public String toString()
    {
        return unitName;
    }
}

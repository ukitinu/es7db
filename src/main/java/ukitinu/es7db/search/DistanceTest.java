package ukitinu.es7db.search;

import junit.framework.TestCase;

public class DistanceTest extends TestCase
{

    public void testConvertLength()
    {
        Distance distance1 = new Distance(10, DistanceUnit.KM);
        assertEquals(10_000.0, distance1.convertLength(DistanceUnit.M), 0.0001);
        assertEquals(1_000_000.0, distance1.convertLength(DistanceUnit.CM), 0.0001);

        Distance distance2 = new Distance(700.1, DistanceUnit.M);
        assertEquals(0.7001, distance2.convertLength(DistanceUnit.KM), 0.0001);
        assertEquals(70010.0, distance2.convertLength(DistanceUnit.CM), 0.0001);

        Distance distance3 = new Distance(80, DistanceUnit.CM);
        assertEquals(0.0008, distance3.convertLength(DistanceUnit.KM), 0.0001);
        assertEquals(0.8, distance3.convertLength(DistanceUnit.M), 0.0001);
    }

    public void testCompareTo()
    {
        Distance distance1 = new Distance(1.07, DistanceUnit.M);
        Distance distance2 = new Distance(600, DistanceUnit.CM);
        assertTrue(distance1.compareTo(distance2) < 0);
        assertTrue(distance2.compareTo(distance1) > 0);
    }
}
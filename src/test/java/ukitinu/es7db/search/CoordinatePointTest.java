package ukitinu.es7db.search;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoordinatePointTest {

    @Test
    void constructorTest() {
        assertDoesNotThrow(()->new CoordinatePoint("1.5, 1.8"));
        assertDoesNotThrow(()->new CoordinatePoint("0.0, 5.5"));
        assertDoesNotThrow(()->new CoordinatePoint("3.3, 0.0"));
        assertDoesNotThrow(()->new CoordinatePoint("0.0, 0.0"));
        assertDoesNotThrow(()->new CoordinatePoint("1.0, 0,1"));
        assertDoesNotThrow(()->new CoordinatePoint("1,11, 0,123"));

        assertDoesNotThrow(()->new CoordinatePoint("0", "0"));
        assertDoesNotThrow(()->new CoordinatePoint("1.5", "1.8"));
        assertDoesNotThrow(()->new CoordinatePoint("0.0", "5.5"));
        assertDoesNotThrow(()->new CoordinatePoint("3.3", "0.0"));
        assertDoesNotThrow(()->new CoordinatePoint("0.0", "0.0"));
        assertDoesNotThrow(()->new CoordinatePoint("1.0", "0,1"));
        assertDoesNotThrow(()->new CoordinatePoint("1,11", "0,123"));

        assertDoesNotThrow(()->new CoordinatePoint("1,11°7.5'   0.8'', 0.789°"));
        assertDoesNotThrow(()->new CoordinatePoint("11.11'', 7,77'"));
        assertDoesNotThrow(()->new CoordinatePoint("0,0°", "0.0'"));
        assertDoesNotThrow(()->new CoordinatePoint("10''", "1° 2'' 3.3'"));
        assertDoesNotThrow(()->new CoordinatePoint("10'' (seconds)", "1° 2'' 3.3' (this will be ignored) (alos, notice how minutes and seconds are out of order)"));
        assertDoesNotThrow(()->new CoordinatePoint("10g 05'12.34'' N", "5g 80'11.22'' E"));

        assertThrows(IllegalArgumentException.class, ()->new CoordinatePoint("", "0"));
        assertThrows(IllegalArgumentException.class, ()->new CoordinatePoint("null", "1.1"));
        assertThrows(IllegalArgumentException.class, ()->new CoordinatePoint("0", ""));
        assertThrows(IllegalArgumentException.class, ()->new CoordinatePoint("invalid", ""));
    }

}
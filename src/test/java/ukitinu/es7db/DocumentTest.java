package ukitinu.es7db;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ukitinu.es7db.exceptions.DocumentException;
import ukitinu.es7db.search.CoordinatePoint;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DocumentTest {

    private static Document doc;
    private static Map<String, Object> obj;

    @BeforeAll
    static void initAll() {
        Map<String, Object> source = new HashMap<>();
        source.put("str", "string value");
        source.put("str_put", "string value");
        source.put("bool", false);
        source.put("double", 10.4);
        source.put("long", 9876543210L);
        source.put("int", 77);
        source.put("coord", "7.77, 11.11");
        source.put("null_key", null);

        obj = new HashMap<>();
        obj.put("array_str", Arrays.asList("val 1", "val 2", "val 3"));
        obj.put("array_long", Arrays.asList(11, -4, 123456789, 2147483648L));
        obj.put("array_int", Arrays.asList(11, -4, 123456789, 16384));
        obj.put("inner_str", "inner string value");
        obj.put("array_coord", Arrays.asList("0, 0", "-50, 19.91", "0.1234, 56.789"));
        obj.put("array_empty", Collections.emptyList());
        obj.put("array_empty_2", new HashMap<>());
        obj.put("array_empty_3", new ArrayList<>());

        source.put("map", obj);
        source.put("map_put", obj);
        source.put("array_map", Arrays.asList(obj, obj, obj));

        doc = new Document("my_index", "my_id", source);
    }

    @Test
    void toEntity() {
    }

    @Test
    void isNullOrEmpty() {
        assertTrue(doc.isNullOrEmpty("fake"));
        assertTrue(doc.isNullOrEmpty("map", "fake_array"));
        assertTrue(doc.isNullOrEmpty("map", "array_empty"));
        assertTrue(doc.isNullOrEmpty("map", "array_empty_2"));
        assertTrue(doc.isNullOrEmpty("map", "array_empty_3"));
        assertTrue(doc.isNullOrEmpty("null_key"));
        assertFalse(doc.isNullOrEmpty("map"));
        assertFalse(doc.isNullOrEmpty("str"));
    }

    @Test
    void put() {
        assertThrows(DocumentException.class, () -> doc.put("new_value_here","fake", "field", "path"));

        assertNull(doc.put("very_new_value", "completely_new_field"));
        assertEquals("very_new_value", doc.get("completely_new_field"));

        assertEquals("string value", doc.put("new string value", "str_put"));
        assertEquals("new string value", doc.get("str_put"));

        assertEquals("inner string value", doc.put("new inner string value", "map_put", "inner_str"));
        assertEquals("new inner string value", doc.get("map_put", "inner_str"));
    }

    @Test
    void get() {
        assertThrows(DocumentException.class, () -> doc.get("fake", "field", "path"));

        assertNull(doc.get("non_existing_field"));

        assertEquals("string value", doc.get("str"));
        assertEquals(10.4, doc.get("double"));
        assertEquals(obj, doc.get("map"));

        boolean myBool = doc.get("bool");
        assertFalse(myBool);

        long myLong = doc.getLong("long");
        assertEquals(9876543210L, myLong);

        long myInt = doc.getLong("int");
        assertEquals(77L, myInt);
    }

    @Test
    void getList() {
        assertEquals(Arrays.asList("val 1", "val 2", "val 3"), doc.getList("map", "array_str"));
        assertEquals(Arrays.asList(11, -4, 123456789, 2147483648L), doc.getList("map", "array_long"));
        assertEquals(Arrays.asList(11, -4, 123456789, 16384), doc.getList("map", "array_int"));
        assertEquals(Arrays.asList(obj, obj, obj), doc.getList("array_map"));
    }

    @Test
    void getLong() {
        assertFalse(doc.get("int") instanceof Long);
        assertEquals(77L, doc.getLong("int"));
    }

    @Test
    void getLongList() {
        assertFalse(doc.getList("map", "array_int").get(0) instanceof Long);
        assertEquals(11L, doc.getLongList("map", "array_int").get(0));
    }

    @Test
    void getCoordinate() {
        assertEquals(new CoordinatePoint(7.77, 11.11), doc.getCoordinate("coord"));
    }

    @Test
    void getCoordinateList() {
        List<CoordinatePoint> list = Arrays.asList(
                new CoordinatePoint(0, 0),
                new CoordinatePoint(-50, 19.91),
                new CoordinatePoint(0.1234, 56.789));
        assertEquals(list, doc.getCoordinateList("map", "array_coord"));
    }
}
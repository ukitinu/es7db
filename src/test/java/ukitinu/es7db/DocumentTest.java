package ukitinu.es7db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
    @SuppressWarnings("unchecked")
    void toEntity() {
        SerialClass obj = SerialClass.getTesterObject();

        Document docObj = new Document("my_index","my_id", obj.toJson());
        assertDoesNotThrow(()->docObj.toEntity(SerialClass.class));

        SerialClass back = docObj.toEntity(SerialClass.class);
        assertEquals(obj, back);
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
        assertThrows(DocumentException.class, () -> doc.put("new_value_here", "fake", "field", "path"));

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
        assertThrows(DocumentException.class, () -> doc.get("non_existing_field"));

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

    private static class SerialClass
    {
        private static final double LAT_1 = 10.0;
        private static final double LAT_2 = 1.47;
        private static final double LON_1 = 12345.6789;
        private static final double LON_2 = -33;
        private static final long TS = 1234567890L;
        private static final CoordinatePoint A = new CoordinatePoint(LAT_1, LON_1);
        private static final CoordinatePoint B = new CoordinatePoint(LAT_1, LON_2);
        private static final CoordinatePoint C = new CoordinatePoint(LAT_2, LON_1);
        private static final CoordinatePoint D = new CoordinatePoint(LAT_2, LON_2);

        private static SerialClass getTesterObject()
        {
            SerialClass obj = new SerialClass();
            obj.bool = true;
            obj.decimal = 1.0;
            obj.id = "id";
            obj.longList = Arrays.asList(0L, 1L, -1L);
            obj.map = Collections.emptyMap();
            obj.mapList = Collections.singletonList(new HashMap<>());
            obj.name = "name";
            obj.point = A;
            obj.pointList = Arrays.asList(A, B, C, D);
            obj.stringList = Arrays.asList("s1", "s2", "s3");
            obj.ts = TS;
            return obj;
        }

        private final ObjectMapper objectMapper = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        private final JSONParser jsonParser = new JSONParser();

        @JsonProperty("id")
        private String id;
        @JsonProperty("ts")
        private long ts;
        @JsonProperty("bool")
        private boolean bool;
        @JsonProperty("name")
        private String name;
        @JsonProperty("decimal")
        private double decimal;
        @JsonProperty("list_s")
        private List<String> stringList;
        @JsonProperty("list_n")
        private List<Long> longList;
        @JsonProperty("list_m")
        private List<Map<String, Object>> mapList;
        @JsonProperty("map")
        private Map<String, Object> map;
        @JsonProperty("point")
        private CoordinatePoint point;
        @JsonProperty("list_p")
        private List<CoordinatePoint> pointList;

        @JsonIgnore
        private JSONObject toJson()
        {
            try {
                return (JSONObject) jsonParser.parse(objectMapper.writeValueAsString(this));
            } catch (JsonProcessingException | ParseException e) {
                return new JSONObject();
            }
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SerialClass that = (SerialClass) o;
            return ts == that.ts &&
                    bool == that.bool &&
                    Double.compare(that.decimal, decimal) == 0 &&
                    id.equals(that.id) &&
                    Objects.equals(name, that.name) &&
                    Objects.equals(stringList, that.stringList) &&
                    Objects.equals(longList, that.longList) &&
                    Objects.equals(mapList, that.mapList) &&
                    Objects.equals(map, that.map) &&
                    Objects.equals(point, that.point) &&
                    Objects.equals(pointList, that.pointList);
        }
    }
}
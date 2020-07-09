package ukitinu.es7db.database.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;

public class SerialisationTest extends TestCase
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

    public void testSerialisation() throws IOException
    {
        SerialClass obj = SerialClass.getTesterObject();
        SerialClass back = SerialClass.toObject(obj.toJson().toString());
        assertEquals(obj, back);
    }

    private static class SerialClass
    {
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

        private static final ObjectMapper MAPPER = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

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

        @JsonIgnore
        private static SerialClass toObject(String source) throws IllegalArgumentException, IOException
        {
            return MAPPER.readValue(source, SerialClass.class);
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


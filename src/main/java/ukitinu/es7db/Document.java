package ukitinu.es7db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import ukitinu.es7db.exceptions.DocumentException;
import ukitinu.es7db.search.CoordinatePoint;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.search.SearchHit;

import java.util.*;
import java.util.stream.Collectors;

public class Document {
    private static final ObjectMapper ENTITY_MAPPER = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final String index;
    private final String id;
    private final Map<String, Object> source;

    public Document(String index, String id, Map<String, Object> source) {
        this.index = index;
        this.id = id;
        this.source = source;
    }

    Document(SearchHit hit) {
        this.index = hit.getIndex();
        this.id = hit.getId();
        this.source = hit.getSourceAsMap();
    }

    Document(GetResponse getResponse) {
        this.index = getResponse.getIndex();
        this.id = getResponse.getId();
        this.source = getResponse.getSourceAsMap();
    }

    public String getIndex() {
        return index;
    }

    public String getId() {
        return id;
    }

    public Map<String, Object> getSource() {
        return new HashMap<>(source);
    }

    public <E> E toEntity(Class<E> entityClass) throws DocumentException {
        try {
            return ENTITY_MAPPER.readValue(new JSONObject(source).toString(), entityClass);
        } catch (JsonProcessingException e) {
            throw new DocumentException("Error converting to " + entityClass.getSimpleName(), index, id);
        }
    }

    public boolean isNullOrEmpty(String field, String... fields) {
        String[] fullPath = buildPath(field, fields);
        return getValue(fullPath) == null;
    }

    public Object put(Object value, String field, String... fields) {
        String[] fullPath = buildPath(field, fields);
        return putValue(value, fullPath);
    }

    //region get
    public <T> T get(String field, String... fields) {
        try {
            return getSingleInternal(field, fields);
        } catch (ClassCastException e) {
            throw new DocumentException(e.getMessage(), index, id, String.join(".", buildPath(field, fields)));
        }
    }

    public <T> List<T> getList(String field, String... fields) {
        try {
            return getListInternal(field, fields);
        } catch (ClassCastException e) {
            throw new DocumentException(e.getMessage(), index, id, String.join(".", buildPath(field, fields)));
        }
    }

    public Long getLong(String field, String... fields) {
        try {
            String numberString = getSingleInternal(field, fields).toString();
            return Long.parseLong(numberString);
        } catch (ClassCastException e) {
            throw new DocumentException(e.getMessage(), index, id, String.join(".", buildPath(field, fields)));
        }
    }

    public List<Long> getLongList(String field, String... fields) {
        try {
            List<Object> list = getListInternal(field, fields);
            return list.stream().map(Object::toString).map(Long::parseLong).collect(Collectors.toList());
        } catch (ClassCastException e) {
            throw new DocumentException(e.getMessage(), index, id, String.join(".", buildPath(field, fields)));
        }
    }

    public CoordinatePoint getCoordinate(String field, String... fields) {
        try {
            String point = get(field, fields);
            return new CoordinatePoint(point);
        } catch (ClassCastException e) {
            throw new DocumentException(e.getMessage(), index, id, String.join(".", buildPath(field, fields)));
        }
    }

    public List<CoordinatePoint> getCoordinateList(String field, String... fields) {
        try {
            List<String> points = getList(field, fields);
            return points
                    .stream()
                    .map(CoordinatePoint::new)
                    .collect(Collectors.toList());
        } catch (ClassCastException e) {
            throw new DocumentException(e.getMessage(), index, id, String.join(".", buildPath(field, fields)));
        }
    }
    //endregion

    //region get_internal
    @SuppressWarnings("unchecked")
    private <T> T getSingleInternal(String field, String... fields) {
        String[] fullPath = buildPath(field, fields);
        Object obj = getValue(fullPath);
        if (obj == null) throw new DocumentException("Null value", index, id, String.join(".", fullPath));
        return (T) obj;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> getListInternal(String field, String... fields) {
        String[] fullPath = buildPath(field, fields);
        Object obj = getValue(fullPath);
        if (obj == null) return Collections.emptyList();
        if (obj instanceof List) {
            return (List<T>) obj;
        } else {
            return Collections.singletonList((T) obj);
        }
    }

    private Object getValue(String... path) {
        Map<String, Object> lastObject = explore(path);
        return lastObject.get(path[path.length - 1]);
    }
    //endregion

    private Object putValue(Object value, String... path) {
        Map<String, Object> lastObject = explore(path);
        return lastObject.put(path[path.length - 1], value);
    }

    private String[] buildPath(String field, String... fields) {
        Objects.requireNonNull(field, "First entry of path must not be null");
        if (fields == null || fields.length == 0) return new String[]{field};
        String[] path = new String[1 + fields.length];
        path[0] = field;
        System.arraycopy(fields, 0, path, 1, path.length - 1);
        return path;
    }

    //region explore
    private Map<String, Object> explore(String... path) {
        StringBuilder sb = new StringBuilder();

        Map<String, Object> current = source;
        int i = 0;
        while (i < path.length - 1) {
            checkPath(current, path[i], sb);
            Object newCurrent = current.get(path[i]);
            current = getMap(newCurrent, sb.toString());
            i++;
        }

        if (!current.containsKey(path[path.length - 1])) {
            throw new DocumentException("Field not found", index, id, String.join(".", path));
        }

        return current;
    }

    private void checkPath(Map<String, Object> current, String field, StringBuilder sb) {
        sb.append(field);
        if (!current.containsKey(field)) {
            throw new DocumentException("Field not found", index, id, sb.toString());
        }
        sb.append(".");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMap(Object obj, String path) {
        if (obj == null) throw new DocumentException("Value is null", index, id, path);
        try {
            return (Map<String, Object>) obj;
        } catch (ClassCastException e) {
            throw new DocumentException("Not a map", index, id, path.substring(0, path.length() - 1));
        }
    }
    //endregion
}

package ukitinu.es7db.database;

import ukitinu.es7db.database.exceptions.DocumentException;
import ukitinu.es7db.database.search.CoordinatePoint;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.search.SearchHit;

import java.util.*;
import java.util.stream.Collectors;


public class Document
{
    private static final String LAT = "lat";
    private static final String LON = "lon";

    private final String index;
    private final String id;
    private final Map<String, Object> source;

    public Document(String index, String id, Map<String, Object> source)
    {
        this.index = index;
        this.id = id;
        this.source = source;
    }

    Document(SearchHit hit)
    {
        this.index = hit.getIndex();
        this.id = hit.getId();
        this.source = hit.getSourceAsMap();
    }

    Document(GetResponse getResponse)
    {
        this.index = getResponse.getIndex();
        this.id = getResponse.getId();
        this.source = getResponse.getSourceAsMap();
    }

    public String getIndex()
    {
        return index;
    }

    public String getId()
    {
        return id;
    }

    public Map<String, Object> getSource()
    {
        return new HashMap<>(source);
    }

    public boolean isNullOrEmpty(String field, String... fields)
    {
        String[] fullPath = buildPath(field, fields);
        return getValue(fullPath) == null;
    }

    public String getString(String field, String... fields)
    {
        try {
            return getSingle(field, fields);
        } catch (ClassCastException e) {
            throw new DocumentException(e.getMessage(), index, id, String.join(".", buildPath(field, fields)));
        }
    }

    public List<String> getStringList(String field, String... fields)
    {
        try {
            return getList(field, fields);
        } catch (ClassCastException e) {
            throw new DocumentException(e.getMessage(), index, id, String.join(".", buildPath(field, fields)));
        }
    }

    public Long getLong(String field, String... fields)
    {
        try {
            String numberString = getSingle(field, fields).toString();
            return Long.parseLong(numberString);
        } catch (ClassCastException e) {
            throw new DocumentException(e.getMessage(), index, id, String.join(".", buildPath(field, fields)));
        }
    }

    public List<Long> getLongList(String field, String... fields)
    {
        try {
            List<Object> list = getList(field, fields);
            return list.stream().map(Object::toString).map(Long::parseLong).collect(Collectors.toList());
        } catch (ClassCastException e) {
            throw new DocumentException(e.getMessage(), index, id, String.join(".", buildPath(field, fields)));
        }
    }

    public Double getDouble(String field, String... fields)
    {
        try {
            return getSingle(field, fields);
        } catch (ClassCastException e) {
            throw new DocumentException(e.getMessage(), index, id, String.join(".", buildPath(field, fields)));
        }
    }


    public List<Double> getDoubleList(String field, String... fields)
    {
        try {
            return getList(field, fields);
        } catch (ClassCastException e) {
            throw new DocumentException(e.getMessage(), index, id, String.join(".", buildPath(field, fields)));
        }
    }


    public Map<String, Object> getMap(String field, String... fields)
    {
        try {
            return getSingle(field, fields);
        } catch (ClassCastException e) {
            throw new DocumentException(e.getMessage(), index, id, String.join(".", buildPath(field, fields)));
        }
    }


    public List<Map<String, Object>> getMapList(String field, String... fields)
    {
        try {
            return getList(field, fields);
        } catch (ClassCastException e) {
            throw new DocumentException(e.getMessage(), index, id, String.join(".", buildPath(field, fields)));
        }
    }


    public Boolean getBoolean(String field, String... fields)
    {
        try {
            return getSingle(field, fields);
        } catch (ClassCastException e) {
            throw new DocumentException(e.getMessage(), index, id, String.join(".", buildPath(field, fields)));
        }
    }


    public List<Boolean> getBooleanList(String field, String... fields)
    {
        try {
            return getList(field, fields);
        } catch (ClassCastException e) {
            throw new DocumentException(e.getMessage(), index, id, String.join(".", buildPath(field, fields)));
        }
    }


    public CoordinatePoint getCoordinate(String field, String... fields)
    {
        try {
            String point = getString(field, fields);
            return new CoordinatePoint(point);
        } catch (ClassCastException e) {
            throw new DocumentException(e.getMessage(), index, id, String.join(".", buildPath(field, fields)));
        }
    }


    public List<CoordinatePoint> getCoordinateList(String field, String... fields)
    {
        try {
            List<String> points = getStringList(field, fields);
            return points
                    .stream()
                    .map(CoordinatePoint::new)
                    .collect(Collectors.toList());
        } catch (ClassCastException e) {
            throw new DocumentException(e.getMessage(), index, id, String.join(".", buildPath(field, fields)));
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getSingle(String field, String... fields)
    {
        String[] fullPath = buildPath(field, fields);
        Object obj = getValue(fullPath);
        if (obj == null) throw new DocumentException("Null value", index, id, String.join(".", fullPath));
        return (T) obj;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> getList(String field, String... fields)
    {
        String[] fullPath = buildPath(field, fields);
        Object obj = getValue(fullPath);
        if (obj == null) return Collections.emptyList();
        if (obj instanceof List) {
            return (List<T>) obj;
        } else {
            return Collections.singletonList((T) obj);
        }
    }


    private Object getValue(String... path)
    {
        StringBuilder sb = new StringBuilder();

        Map<String, Object> current = source;
        int i = 0;
        while (i < path.length - 1) {
            Object newCurrent = getValue(current, path[i], sb);
            current = getMap(newCurrent, sb.toString());
            i++;
        }
        return getValue(current, path[i], sb);
    }


    private String[] buildPath(String field, String... fields)
    {
        Objects.requireNonNull(field, "First entry of path must not be null");
        if (fields == null || fields.length == 0) return new String[]{field};
        String[] path = new String[1 + fields.length];
        path[0] = field;
        System.arraycopy(fields, 0, path, 1, path.length - 1);
        return path;
    }


    private Object getValue(Map<String, Object> current, String field, StringBuilder path)
    {
        checkPath(current, field, path);
        return current.get(field);
    }

    private void checkPath(Map<String, Object> current, String field, StringBuilder path)
    {
        path.append(field);
        if (!current.containsKey(field)) {
            throw new DocumentException("Field not found", index, id, path.toString());
        }
        path.append(".");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMap(Object obj, String path)
    {
        if (obj == null) throw new DocumentException("Value is null", index, id, path);
        try {
            return (Map<String, Object>) obj;
        } catch (ClassCastException e) {
            throw new DocumentException("Not a map", index, id, path.substring(0, path.length() - 1));
        }
    }
}

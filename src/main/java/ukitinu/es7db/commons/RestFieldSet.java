package ukitinu.es7db.commons;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class RestFieldSet
{
    private static final RestFieldSet EMPTY_SET = new RestFieldSet();

    private final Set<RestField<?>> restFields = new HashSet<>();

    static RestFieldSet emptySet()
    {
        return EMPTY_SET;
    }

    Set<RestField<?>> getSet()
    {
        return new HashSet<>(restFields);
    }

    RestFieldSet addString(String fieldName)
    {
        restFields.add(new RestField<>(fieldName, String.class));
        return this;
    }

    RestFieldSet addInt(String fieldName)
    {
        restFields.add(new RestField<>(fieldName, Integer.class));
        return this;
    }

    RestFieldSet addLong(String fieldName)
    {
        restFields.add(new RestField<>(fieldName, Long.class));
        return this;
    }

    RestFieldSet addDouble(String fieldName)
    {
        restFields.add(new RestField<>(fieldName, Double.class));
        return this;
    }

    RestFieldSet addList(String fieldName)
    {
        restFields.add(new RestField<>(fieldName, List.class));
        return this;
    }

    RestFieldSet addMap(String fieldName)
    {
        restFields.add(new RestField<>(fieldName, Map.class));
        return this;
    }

    RestFieldSet addAll(RestFieldSet... otherSets)
    {
        if (otherSets != null && otherSets.length > 0) {
            for (RestFieldSet set : otherSets) {
                if (set != null) restFields.addAll(set.getSet());
            }
        }
        return this;
    }
}

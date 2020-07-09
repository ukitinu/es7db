package ukitinu.es7db.commons;

import java.util.Objects;

class RestField<T>
{
    private final String name;
    private final Class<T> type;

    RestField(String name, Class<T> type)
    {
        this.name = name;
        this.type = type;
    }

    String getName()
    {
        return name;
    }

    boolean isWrongType(Object object)
    {
        if (type == Long.class) {
            return !(object instanceof Long) && !(object instanceof Integer);
        }
        return !type.isInstance(object);
    }

    @Override
    public String toString()
    {
        return toString(false);
    }

    public String toString(boolean isOptional)
    {
        String field = name + " (" + type.getSimpleName() + ")";
        return isOptional ? "OPTIONAL " + field : field;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestField<?> restField = (RestField<?>) o;
        return name.equals(restField.name);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name);
    }
}

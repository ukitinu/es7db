package ukitinu.es7db.search;

import org.elasticsearch.search.sort.SortOrder;

import java.util.Objects;

class SortElement
{
    private final String field;
    private final boolean isAscending;

    SortElement(String field, boolean isAscending)
    {
        this.field = field;
        this.isAscending = isAscending;
    }

    String getField()
    {
        return field;
    }

    SortOrder getSortOrder()
    {
        return isAscending ? SortOrder.ASC : SortOrder.DESC;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SortElement that = (SortElement) o;
        return isAscending == that.isAscending &&
                Objects.equals(field, that.field);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(field, isAscending);
    }
}

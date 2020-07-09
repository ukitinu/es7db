package ukitinu.es7db.database.search;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

class TermElement implements Queryable
{
    private static final String NO_VALUES_ERROR = "Term query: no values found";

    private final String field;
    private final Object[] values;

    TermElement(String field, Object... values)
    {
        if (values == null || values.length == 0) throw new IllegalArgumentException(NO_VALUES_ERROR);
        this.field = field;
        this.values = values;
    }

    public QueryBuilder toQuery()
    {
        return values.length == 1 ? QueryBuilders.termQuery(field, values[0]) : QueryBuilders.termsQuery(field, values);
    }

}


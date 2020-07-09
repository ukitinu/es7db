package ukitinu.es7db.database.search;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

class RangeElement implements Queryable
{
    private final String field;
    private final RangeParameter parameter;
    private final Object value;

    RangeElement(String field, Object value, RangeParameter parameter)
    {
        this.field = field;
        this.value = value;
        this.parameter = parameter;
    }

    @Override
    public QueryBuilder toQuery()
    {
        return parameter.getRangeFunction(QueryBuilders.rangeQuery(field)).apply(value);
    }
}

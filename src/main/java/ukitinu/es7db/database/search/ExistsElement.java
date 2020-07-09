package ukitinu.es7db.database.search;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class ExistsElement implements Queryable
{
    private final String field;

    ExistsElement(String field)
    {
        this.field = field;
    }

    public QueryBuilder toQuery()
    {
        return QueryBuilders.existsQuery(field);
    }
}

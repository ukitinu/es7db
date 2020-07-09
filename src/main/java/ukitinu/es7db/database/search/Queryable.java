package ukitinu.es7db.database.search;

import org.elasticsearch.index.query.QueryBuilder;

public interface Queryable
{
    QueryBuilder toQuery();
}

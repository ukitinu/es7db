package ukitinu.es7db.search;

import org.elasticsearch.index.query.QueryBuilder;

public interface Queryable
{
    QueryBuilder toQuery();
}

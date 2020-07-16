package ukitinu.es7db.search;

import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import ukitinu.es7db.config.Property;

public class MatchElement implements Queryable
{
    private static final int FUZZY_PREFIX = Property.ES_SEARCH_FUZZY_PREFIX.getInt();
    private static final int FUZZY_MAX_EXPANSION = Property.ES_SEARCH_FUZZY_MAX_EXP.getInt();

    private final String field;
    private final String text;
    private final boolean isFuzzy;

    MatchElement(String field, String text, boolean isFuzzy)
    {
        this.field = field;
        this.text = text;
        this.isFuzzy = isFuzzy;
    }

    @Override
    public QueryBuilder toQuery()
    {
        if (isFuzzy) {
            return QueryBuilders.matchQuery(field, text)
                    .operator(Operator.OR)
                    .fuzziness(Fuzziness.AUTO)
                    .prefixLength(FUZZY_PREFIX)
                    .maxExpansions(FUZZY_MAX_EXPANSION);
        } else {
            return QueryBuilders.matchQuery(field, text);
        }
    }
}

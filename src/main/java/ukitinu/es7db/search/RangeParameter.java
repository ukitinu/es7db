package ukitinu.es7db.search;


import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;

import java.util.function.Function;

public enum RangeParameter
{
    GT("gt"), GTE("gte"), LT("lt"), LTE("lte");

    private final String param;

    RangeParameter(String param)
    {
        this.param = param;
    }

    public static RangeParameter fromString(String param)
    {
        RangeParameter[] values = values();
        for (RangeParameter value : values) {
            if (value.param.equals(param)) {
                return value;
            }
        }
        throw new IllegalArgumentException("No matching parameter for [" + param + "]");
    }

    Function<Object, QueryBuilder> getRangeFunction(RangeQueryBuilder builder)
    {
        switch (this) {
            case GT:
                return builder::gt;
            case GTE:
                return builder::gte;
            case LT:
                return builder::lt;
            case LTE:
                return builder::lte;
            default:
                throw new IllegalArgumentException("No range function for [" + name() + "]");
        }
    }
}

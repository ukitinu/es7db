package ukitinu.es7db;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.IncludeExclude;

import java.util.List;

final class Aggregations
{
    private Aggregations()
    {
        throw new IllegalStateException("Utility class");
    }

    static AggregationBuilder newTermAggregation(String field, int size, List<String> excludeKw)
    {
        return AggregationBuilders.terms(field)
                .field(field)
                .size(size)
                .includeExclude(new IncludeExclude(null, excludeKw.toArray(new String[0])));
    }

    static AggregationBuilder newDateHistogramAggregation(String field, HistogramInterval interval)
    {
        DateHistogramAggregationBuilder builder = AggregationBuilders
                .dateHistogram(field)
                .field(field)
                .minDocCount(0)
                .extendedBounds(interval.getExtendedBounds());
        return interval.isCalendar() ? builder.calendarInterval(interval.getInterval()) : builder.fixedInterval(interval.getInterval());
    }
}

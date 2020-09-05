package ukitinu.es7db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class HistogramInterval
{

    private static final Logger LOG = LogManager.getLogger(HistogramInterval.class);
    private static final String BAD_INTERVAL_WARN = "Date Histogram interval \"{}\" not supported, defaulting to \"{}\"";

    private static final DateHistogramInterval DEFAULT_INTERVAL = DateHistogramInterval.DAY;

    private static final String STRING_INTERVAL_MINUTE = "minute";
    private static final String STRING_INTERVAL_HOUR = "hour";
    private static final String STRING_INTERVAL_DAY = "day";
    private static final String STRING_INTERVAL_WEEK = "week";
    private static final String STRING_INTERVAL_MONTH = "month";
    private static final String STRING_INTERVAL_QUARTER = "quarter";
    private static final String STRING_INTERVAL_YEAR = "year";

    private static final String FIXED_INTERVALS_REGEX = "\\b\\d{1,3}[smhd]\\b";
    private static final Pattern FIXED_INTERVALS_PATTERN = Pattern.compile(FIXED_INTERVALS_REGEX);

    private static final Map<String, DateHistogramInterval> CALENDAR_INTERVALS = new HashMap<>();

    static {
        CALENDAR_INTERVALS.put(STRING_INTERVAL_MINUTE, DateHistogramInterval.MINUTE);
        CALENDAR_INTERVALS.put(STRING_INTERVAL_HOUR, DateHistogramInterval.HOUR);
        CALENDAR_INTERVALS.put(STRING_INTERVAL_DAY, DateHistogramInterval.DAY);
        CALENDAR_INTERVALS.put(STRING_INTERVAL_WEEK, DateHistogramInterval.WEEK);
        CALENDAR_INTERVALS.put(STRING_INTERVAL_MONTH, DateHistogramInterval.MONTH);
        CALENDAR_INTERVALS.put(STRING_INTERVAL_QUARTER, DateHistogramInterval.QUARTER);
        CALENDAR_INTERVALS.put(STRING_INTERVAL_YEAR, DateHistogramInterval.YEAR);
    }

    private final long minBound;
    private final long maxBound;
    private final DateHistogramInterval interval;
    private boolean isCalendar;

    public HistogramInterval(long minBound, long maxBound, String interval)
    {
        if (minBound < 0 || maxBound < minBound) {
            throw new IllegalArgumentException("Negative minBound or maxBound < minBound");
        }
        this.minBound = minBound;
        this.maxBound = maxBound;
        this.interval = getDateHistogramInterval(interval);
    }

    ExtendedBounds getExtendedBounds()
    {
        return new ExtendedBounds(minBound, maxBound);
    }

    DateHistogramInterval getInterval()
    {
        return interval;
    }

    boolean isCalendar()
    {
        return isCalendar;
    }

    private DateHistogramInterval getDateHistogramInterval(String expression)
    {
        if (CALENDAR_INTERVALS.containsKey(expression)) {
            isCalendar = true;
            return CALENDAR_INTERVALS.get(expression);
        } else if (FIXED_INTERVALS_PATTERN.matcher(expression).find()) {
            isCalendar = false;
            return new DateHistogramInterval(expression);
        } else {
            LOG.warn(BAD_INTERVAL_WARN, expression, DEFAULT_INTERVAL.toString());
            isCalendar = true;
            return DEFAULT_INTERVAL;
        }
    }
}

package ukitinu.es7db.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Property
{
    COORDINATE_PRECISION("coordinates_decimal", "4"),
    ES_HOST("es.host", "localhost"),
    ES_PORT("es.port", "9200"),
    ES_BULK_MAX_SIZE("es.bulk.max_size", "500"),
    ES_BULK_FLUSH_MB("es.bulk.flush_mb", "1"),
    ES_BULK_FLUSH_TIME("es.bulk.flush_secs", "5"),
    ES_BULK_CONCURRENTS("es.bulk.concurrents", "3"),
    ES_BACKOFF_TIME("es.backoff.time", "100"),
    ES_BACKOFF_TRIES("es.backoff.tries", "3"),
    ES_UPDATE_CONFLICT_RETRY("es.update_conflict_retry", "3"),
    ES_SEARCH_FUZZY_PREFIX("es.search.fuzzy.prefix", "0"),
    ES_SEARCH_FUZZY_MAX_EXP("es.search.fuzzy.max_expansion", "50"),
    ES_SEARCH_FROM("es.search.from", "0"),
    ES_SEARCH_SIZE("es.search.size", "10"),
    ES_SEARCH_MIN_SHOULD("es.search.min_should", "1");

    private final String name;
    private final String defaultValue;
    private String value;

    Property(String name, String defaultValue)
    {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    String getName()
    {
        return name;
    }

    void setValue(String value)
    {
        if (value == null) {
            LOG.warn(UNABLE_TO_FIND + name + USING_DEFAULT + defaultValue);
        } else {
            LOG.info(name + PROP_SET_TO + value);
        }
        this.value = value;
    }

    public String getString()
    {
        if (value == null) {
            LOG.debug(UNABLE_TO_FIND + name + USING_DEFAULT + defaultValue);
            return defaultValue;
        }
        return value;
    }

    public int getInt()
    {
        return Integer.parseInt(getString());
    }

    private static final Logger LOG = LoggerFactory.getLogger(PropertiesReader.class);

    private static final String UNABLE_TO_FIND = "Unable to find ";
    private static final String USING_DEFAULT = ", using default value ";
    private static final String PROP_SET_TO = " set to ";
}

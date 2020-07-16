package ukitinu.es7db;

import ukitinu.es7db.config.Property;

public final class DatabaseConstants
{
    private DatabaseConstants()
    {
        throw new IllegalStateException("Constants class");
    }

    /* properties */
    static final String DB_SCHEME = "http";
    static final String HOSTNAME = Property.ES_HOST.getString();
    static final int HTTP_PORT = Property.ES_PORT.getInt();

    /* logs */
    static final String LOG_BULK_ID_ACTIONS = "Bulk [{}] {} requests";
    static final String LOG_BULK_ERROR = "Bulk [{}] error: {}.";
    static final String LOG_BULK_EXECUTED = "Bulk [{}] {}.";
    static final String LOG_BULK_EXECUTED_GOOD = "succeeded";
    static final String LOG_BULK_EXECUTED_FAIL = "failed";

    /* config */
    static final int ES_CONFIG_BULK_MAX_SIZE = Property.ES_BULK_MAX_SIZE.getInt();
    static final int ES_CONFIG_BULK_FLUSH_SIZE = Property.ES_BULK_FLUSH_MB.getInt();
    static final int ES_CONFIG_BULK_FLUSH_TIME = Property.ES_BULK_FLUSH_TIME.getInt();
    static final int ES_CONFIG_BULK_CONCURRENTS = Property.ES_BULK_CONCURRENTS.getInt();
    static final int ES_CONFIG_BACKOFF_TIME = Property.ES_BACKOFF_TIME.getInt();
    static final int ES_CONFIG_BACKOFF_TRIES = Property.ES_BACKOFF_TRIES.getInt();

    /* requests */
    static final String STORED_FIELDS_NONE = "_none_";
    static final int RETRY_ON_CONFLICT_TIMES = Property.ES_UPDATE_CONFLICT_RETRY.getInt();

    /* queries */
    public static final int MINIMUM_SHOULD_DEFAULT = Property.ES_SEARCH_MIN_SHOULD.getInt();
    public static final int MINIMUM_SHOULD_UNDEFINED = 0;

}

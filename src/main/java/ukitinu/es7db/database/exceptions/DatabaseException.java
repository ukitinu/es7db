package ukitinu.es7db.database.exceptions;


import org.apache.http.HttpStatus;
import org.elasticsearch.ElasticsearchException;

public class DatabaseException extends Exception
{
    private static final String ES_EXCEPTION = "Database client exception: ";
    private static final long serialVersionUID = 5702123196618950745L;

    private final int status;

    public static DatabaseException wrapException(Exception e)
    {
        if (e instanceof ElasticsearchException) {
            String causeMessage = getElasticExceptionMessage((ElasticsearchException) e);
            int statusNumber = ((ElasticsearchException) e).status().getStatus();
            return new DatabaseException(causeMessage, e, statusNumber);
        } else {
            return new DatabaseException(e.getMessage(), e, HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private DatabaseException(String message, Throwable cause, int status)
    {
        super(message, cause);
        this.status = status;
    }

    public DatabaseException(String message, int status)
    {
        this(message, null, status);
    }

    public int getStatus()
    {
        return status;
    }

    private static String getElasticExceptionMessage(ElasticsearchException e)
    {
        try {
            return ES_EXCEPTION + e.getCause().getMessage();
        } catch (Exception ex) {
            return e.getMessage();
        }
    }
}

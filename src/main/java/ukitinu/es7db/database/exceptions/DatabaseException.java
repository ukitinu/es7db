package ukitinu.es7db.database.exceptions;


import org.elasticsearch.ElasticsearchException;
import org.springframework.http.HttpStatus;
import ukitinu.es7db.commons.WithHttpStatus;

public class DatabaseException extends Exception implements WithHttpStatus
{
    private static final String ES_EXCEPTION = "Database client exception: ";
    private static final long serialVersionUID = 5702123196618950745L;

    private final HttpStatus status;

    public static DatabaseException wrapException(Exception e)
    {
        if (e instanceof ElasticsearchException) {
            String causeMessage = getElasticExceptionMessage((ElasticsearchException) e);
            int statusNumber = ((ElasticsearchException) e).status().getStatus();
            return new DatabaseException(causeMessage, e, HttpStatus.valueOf(statusNumber));
        } else {
            return new DatabaseException(e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private DatabaseException(String message, Throwable cause, HttpStatus status)
    {
        super(message, cause);
        this.status = status;
    }

    public DatabaseException(String message, HttpStatus status)
    {
        this(message, null, status);
    }

    public HttpStatus getStatus()
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

package ukitinu.es7db.database.exceptions;

public class DocumentException extends RuntimeException
{
    private static final long serialVersionUID = 1861037683055409186L;

    private final String index;
    private final String id;
    private final String path;

    public DocumentException(String message, String index, String id, String path)
    {
        super(message);
        this.index = index;
        this.id = id;
        this.path = path;
    }

    @Override
    public String getMessage()
    {
        return super.getMessage() + ": " + index + "/" + id + "[\"" + path + "\"]";
    }
}

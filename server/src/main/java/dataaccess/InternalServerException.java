package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class InternalServerException extends DataAccessException{
    public InternalServerException() {
        super("Error: Internal Server Error");
    }
    public InternalServerException(String message) {
        super(message);
    }
}

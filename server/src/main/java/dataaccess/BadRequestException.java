package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class BadRequestException extends DataAccessException{
    public BadRequestException() {
        super("Error: bad request");
    }
    public BadRequestException(String message) {
        super(message);
    }
}

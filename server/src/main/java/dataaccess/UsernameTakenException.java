package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class UsernameTakenException extends DataAccessException{
    public UsernameTakenException() {
        super("Username already taken");
    }
    public UsernameTakenException(String message) {
        super(message);
    }
}

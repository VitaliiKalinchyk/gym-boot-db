package epam.task.gymbootdb.exception;

public class UserException extends RuntimeException {
    public UserException(String username) {
        super("User with username " + username + " was not found");
    }
}
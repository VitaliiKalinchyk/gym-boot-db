package epam.task.gymbootdb.exception;

public class UserException extends NotFoundException {
    public UserException(String username) {
        super("User with username " + username + " was not found");
    }
}
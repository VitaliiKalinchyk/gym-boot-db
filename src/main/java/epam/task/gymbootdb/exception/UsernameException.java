package epam.task.gymbootdb.exception;

public class UsernameException extends RuntimeException{
    public UsernameException(String username) {
        super("Username " + username + " already in use");
    }
}
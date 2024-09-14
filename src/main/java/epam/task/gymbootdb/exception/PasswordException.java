package epam.task.gymbootdb.exception;

public class PasswordException extends RuntimeException {

    public PasswordException() {
        super("Wrong password");
    }
}
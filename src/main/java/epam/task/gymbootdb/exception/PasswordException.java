package epam.task.gymbootdb.exception;

import org.springframework.http.HttpStatus;

public class PasswordException extends GymResponseStatusException {
    public PasswordException() {
        super(HttpStatus.UNAUTHORIZED, "Wrong password");
    }
}

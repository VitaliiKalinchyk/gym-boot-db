package epam.task.gymbootdb.exception;

import org.springframework.http.HttpStatus;

public class LoginAttemptException extends GymResponseStatusException {
    public LoginAttemptException(String username) {
        super(HttpStatus.FORBIDDEN, username + " is locked. Please try again later.");
    }
}

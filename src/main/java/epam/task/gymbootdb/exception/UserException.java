package epam.task.gymbootdb.exception;

import org.springframework.http.HttpStatus;

public class UserException extends GymResponseStatusException {
    public UserException(String username) {
        super(HttpStatus.NOT_FOUND, "User with username " + username + " was not found");
    }
}
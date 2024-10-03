package epam.task.gymbootdb.exception;

import org.springframework.http.HttpStatus;

public class TrainerException extends GymResponseStatusException {

    public TrainerException(long id) {
        super(HttpStatus.NOT_FOUND, "Trainer with id " + id + " was not found");
    }

    public TrainerException(String username) {
        super(HttpStatus.NOT_FOUND, "Trainer with username " + username + " was not found");
    }
}

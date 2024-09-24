package epam.task.gymbootdb.exception;

import org.springframework.http.HttpStatus;

public class TrainingTypeException extends GymResponseStatusException {
    public TrainingTypeException(long id) {
        super(HttpStatus.NOT_FOUND, "TrainingType with id " + id + " does not exist");
    }
}

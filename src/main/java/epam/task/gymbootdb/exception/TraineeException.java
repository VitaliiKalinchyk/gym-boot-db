package epam.task.gymbootdb.exception;

import org.springframework.http.HttpStatus;

public class TraineeException extends GymResponseStatusException {

    public TraineeException(long id) {
        super(HttpStatus.NOT_FOUND, "Trainee with id " + id + " was not found");
    }

    public TraineeException(String username) {
        super(HttpStatus.NOT_FOUND, "Trainee with username " + username + " was not found");
    }
}

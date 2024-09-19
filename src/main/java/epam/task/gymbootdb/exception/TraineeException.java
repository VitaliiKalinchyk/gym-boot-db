package epam.task.gymbootdb.exception;

public class TraineeException extends RuntimeException{

    public TraineeException(long id) {
        super("Trainee with id " + id + " was not found");
    }
}
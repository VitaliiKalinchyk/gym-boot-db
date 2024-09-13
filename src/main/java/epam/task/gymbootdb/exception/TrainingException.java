package epam.task.gymbootdb.exception;

public class TrainingException extends RuntimeException{

    public TrainingException(long id) {
        super("Training with id " + id + " was not found");
    }
}
package epam.task.gymbootdb.exception;

public class TrainerException extends NotFoundException{
    public TrainerException(long id) {
        super("Trainer with id " + id + " was not found");
    }
}
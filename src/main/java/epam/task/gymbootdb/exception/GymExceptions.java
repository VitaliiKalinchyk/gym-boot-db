package epam.task.gymbootdb.exception;

public class GymExceptions {

    public static TraineeException noSuchTrainee(long id) {
        return new TraineeException(id);
    }

    public static TraineeException noSuchTrainee(String username) {
        return new TraineeException(username);
    }

    public static TrainerException noSuchTrainer(long id) {
        return new TrainerException(id);
    }

    public static TrainerException noSuchTrainer(String username) {
        return new TrainerException(username);
    }

    public static PasswordException wrongPassword() {
        return new PasswordException();
    }

    private GymExceptions() {}
}
package epam.task.gymbootdb.service;

import epam.task.gymbootdb.entity.Trainee;

import java.util.List;

public interface TraineeService {

    Trainee createProfile(Trainee trainee);

    boolean matchCredentials(String username, String password);

    Trainee update(Trainee trainee);

    void changePassword(String username, String newPassword);

    void setActiveStatus(String username, boolean isActive);

    Trainee getById(long id);

    Trainee getByUsername(String username);

    List<Trainee> getAll();

    void delete(long id);

    void deleteByUsername(String username);

    Trainee updateTraineeTrainers(String traineeUsername, List<Long> trainerIds);
}
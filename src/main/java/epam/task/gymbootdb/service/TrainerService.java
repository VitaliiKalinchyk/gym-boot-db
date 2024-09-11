package epam.task.gymbootdb.service;

import epam.task.gymbootdb.entity.Trainer;

import java.util.List;

public interface TrainerService {

    Trainer createProfile(Trainer trainer);

    boolean matchCredentials(String username, String password);

    Trainer update(Trainer trainer);

    void changePassword(String username, String newPassword);

    void setActiveStatus(String username, boolean isActive);

    Trainer getById(long id);

    Trainer getByUsername(String username);

    List<Trainer> getAll();

    List<Trainer> getTrainersNotAssignedToTrainee(String traineeUsername);
}
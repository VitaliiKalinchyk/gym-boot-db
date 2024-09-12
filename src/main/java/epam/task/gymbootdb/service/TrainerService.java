package epam.task.gymbootdb.service;

import epam.task.gymbootdb.dto.TrainerCreateOrUpdateRequest;
import epam.task.gymbootdb.dto.TrainerResponse;
import epam.task.gymbootdb.dto.UserCredentials;

import java.util.List;

public interface TrainerService {

    UserCredentials createProfile(TrainerCreateOrUpdateRequest trainer);

    boolean matchCredentials(UserCredentials user);

    TrainerResponse update(TrainerCreateOrUpdateRequest trainer);

    void changePassword(UserCredentials user);

    void setActiveStatus(String username, boolean isActive);

    TrainerResponse getById(long id);

    TrainerResponse getByUsername(String username);

    List<TrainerResponse> getAll();

    List<TrainerResponse> getTrainersNotAssignedToTrainee(String traineeUsername);
}
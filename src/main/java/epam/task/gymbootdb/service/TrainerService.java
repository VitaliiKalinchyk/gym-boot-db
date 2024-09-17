package epam.task.gymbootdb.service;

import epam.task.gymbootdb.dto.ChangePasswordRequest;
import epam.task.gymbootdb.dto.TrainerCreateOrUpdateRequest;
import epam.task.gymbootdb.dto.TrainerResponse;
import epam.task.gymbootdb.dto.UserCredentials;

import java.util.List;

public interface TrainerService {

    UserCredentials createProfile(TrainerCreateOrUpdateRequest trainer);

    void matchCredentials(UserCredentials user);

    TrainerResponse update(TrainerCreateOrUpdateRequest trainer);

    void changePassword(ChangePasswordRequest request);

    void setActiveStatus(long id, boolean isActive);

    TrainerResponse getById(long id);

    List<TrainerResponse> getTrainersNotAssignedToTrainee(long id);
}
package epam.task.gymbootdb.service;

import epam.task.gymbootdb.dto.ChangePasswordRequest;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserCredentials;

import java.util.List;

public interface TrainerService {

    UserCredentials createProfile(TrainerDto trainer);

    void matchCredentials(UserCredentials user);

    TrainerDto update(TrainerDto trainer);

    void changePassword(ChangePasswordRequest request);

    void setActiveStatus(long id, boolean isActive);

    TrainerDto getById(long id);

    List<TrainerDto> getTrainersNotAssignedToTrainee(long id);
}
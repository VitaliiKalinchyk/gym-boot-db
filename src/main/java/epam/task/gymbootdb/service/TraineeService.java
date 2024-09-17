package epam.task.gymbootdb.service;


import epam.task.gymbootdb.dto.ChangePasswordRequest;
import epam.task.gymbootdb.dto.TraineeCreateOrUpdateRequest;
import epam.task.gymbootdb.dto.TraineeResponse;
import epam.task.gymbootdb.dto.UserCredentials;

public interface TraineeService {

    UserCredentials createProfile(TraineeCreateOrUpdateRequest trainee);

    void matchCredentials(UserCredentials user);

    TraineeResponse update(TraineeCreateOrUpdateRequest trainee);

    void changePassword(ChangePasswordRequest request);

    void setActiveStatus(long id, boolean isActive);

    TraineeResponse getById(long id);

    void deleteById(long id);

    void updateTraineeTrainers(long traineeId, long trainerId);
}
package epam.task.gymbootdb.service;


import epam.task.gymbootdb.dto.ChangePasswordRequest;
import epam.task.gymbootdb.dto.TraineeDto;
import epam.task.gymbootdb.dto.UserCredentials;

public interface TraineeService {

    UserCredentials createProfile(TraineeDto trainee);

    void matchCredentials(UserCredentials user);

    TraineeDto update(TraineeDto trainee);

    void changePassword(ChangePasswordRequest request);

    void setActiveStatus(long id, boolean isActive);

    TraineeDto getById(long id);

    void deleteById(long id);

    void updateTraineeTrainers(long traineeId, long trainerId);
}
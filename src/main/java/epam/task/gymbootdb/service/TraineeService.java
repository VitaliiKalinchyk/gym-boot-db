package epam.task.gymbootdb.service;

import epam.task.gymbootdb.dto.TraineeCreateOrUpdateRequest;
import epam.task.gymbootdb.dto.TraineeResponse;
import epam.task.gymbootdb.dto.UserCredentials;

import java.util.List;

public interface TraineeService {

    UserCredentials createProfile(TraineeCreateOrUpdateRequest trainee);

    boolean matchCredentials(UserCredentials user);

    TraineeResponse update(TraineeCreateOrUpdateRequest trainee);

    void changePassword(UserCredentials user);

    void setActiveStatus(String username, boolean isActive);

    TraineeResponse getById(long id);

    TraineeResponse getByUsername(String username);

    List<TraineeResponse> getAll();

    void delete(long id);

    void deleteByUsername(String username);

    void updateTraineeTrainers(String traineeUsername, List<Long> trainerIds);
}
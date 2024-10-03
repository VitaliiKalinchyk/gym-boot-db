package epam.task.gymbootdb.service;


import epam.task.gymbootdb.dto.TraineeDto;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserCredentials;

import java.util.List;

public interface TraineeService {

    UserCredentials createProfile(TraineeDto trainee);

    TraineeDto update(TraineeDto trainee);

    TraineeDto getByUsername(String username);

    void deleteByUsername(String username);

    List<TrainerDto> getTrainersNotAssignedToTrainee(String username);

    void updateTraineeTrainers(String username, long trainerId);
}

package epam.task.gymbootdb.service;


import epam.task.gymbootdb.dto.TraineeDto;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserCredentials;

import java.util.List;

public interface TraineeService {

    UserCredentials createProfile(TraineeDto trainee);

    TraineeDto update(TraineeDto trainee);

    void setActiveStatus(long id, boolean isActive);

    TraineeDto getById(long id);

    void deleteById(long id);

    List<TrainerDto> getTrainersNotAssignedToTrainee(long id);

    void updateTraineeTrainers(long traineeId, long trainerId);
}
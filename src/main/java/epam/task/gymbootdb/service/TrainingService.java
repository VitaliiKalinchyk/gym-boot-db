package epam.task.gymbootdb.service;

import epam.task.gymbootdb.dto.TraineeTrainingsRequest;
import epam.task.gymbootdb.dto.TrainerTrainingsRequest;
import epam.task.gymbootdb.dto.TrainingDto;

import java.util.List;

public interface TrainingService {

    void create(TrainingDto training);

    List<TrainingDto> getTraineeTrainings(TraineeTrainingsRequest request);

    List<TrainingDto> getTrainerTrainings(TrainerTrainingsRequest request);
}

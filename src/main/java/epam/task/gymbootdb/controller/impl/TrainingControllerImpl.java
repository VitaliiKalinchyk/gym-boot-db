package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.controller.TrainingController;
import epam.task.gymbootdb.dto.TraineeTrainingsRequest;
import epam.task.gymbootdb.dto.TrainerTrainingsRequest;
import epam.task.gymbootdb.dto.TrainingDto;
import epam.task.gymbootdb.service.TrainingService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TrainingControllerImpl implements TrainingController {

    private final TrainingService trainingService;

    @Override
    public ResponseEntity<Void> create(TrainingDto trainingDto) {
        trainingService.create(trainingDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<List<TrainingDto>> getTraineeTrainings(long id, LocalDate fromDate, LocalDate toDate,
                                                                 Long trainerId, Long trainingTypeId) {
        TraineeTrainingsRequest request = TraineeTrainingsRequest.builder()
                .traineeId(id)
                .fromDate(fromDate)
                .toDate(toDate)
                .trainerId(trainerId)
                .trainingTypeId(trainingTypeId)
                .build();
        List<TrainingDto> trainings = trainingService.getTraineeTrainings(request);

        return ResponseEntity.ok(trainings);
    }

    @Override
    public ResponseEntity<List<TrainingDto>> getTrainerTrainings(long id, LocalDate fromDate, LocalDate toDate,
                                                                 Long trainerId) {
        TrainerTrainingsRequest request = TrainerTrainingsRequest.builder()
                .trainerId(id)
                .fromDate(fromDate)
                .toDate(toDate)
                .traineeId(trainerId)
                .build();
        List<TrainingDto> trainings = trainingService.getTrainerTrainings(request);

        return ResponseEntity.ok(trainings);
    }
}

package epam.task.gymbootdb.controller;

import epam.task.gymbootdb.dto.TraineeTrainingsRequest;
import epam.task.gymbootdb.dto.TrainerTrainingsRequest;
import epam.task.gymbootdb.dto.TrainingDto;
import epam.task.gymbootdb.service.TrainingService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/trainings")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody TrainingDto trainingDto) {
        trainingService.create(trainingDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/trainee/{id}")
    public ResponseEntity<List<TrainingDto>> getTraineeTrainings(
            @PathVariable long id,
            @RequestParam(name = "from-date", required = false) LocalDate fromDate,
            @RequestParam(name = "to-date", required = false) LocalDate toDate,
            @RequestParam(name = "trainer-id", required = false) Long trainerId,
            @RequestParam(name = "training-type-id", required = false) Long trainingTypeId) {
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

    @GetMapping("/trainer/{id}")
    public ResponseEntity<List<TrainingDto>> getTrainerTrainings(
            @PathVariable long id,
            @RequestParam(name = "from-date", required = false) LocalDate fromDate,
            @RequestParam(name = "to-date", required = false) LocalDate toDate,
            @RequestParam(name = "trainee-id", required = false) Long trainerId) {
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
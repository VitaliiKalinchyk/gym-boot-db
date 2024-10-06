package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.controller.TrainingController;
import epam.task.gymbootdb.dto.TraineeTrainingsRequest;
import epam.task.gymbootdb.dto.TrainerTrainingsRequest;
import epam.task.gymbootdb.dto.TrainingDto;
import epam.task.gymbootdb.service.LoggingService;
import epam.task.gymbootdb.service.TrainingService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trainings")
public class TrainingControllerImpl implements TrainingController {

    private final TrainingService trainingService;
    private final LoggingService loggingService;

    @Override
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody TrainingDto trainingDto) {
        trainingService.create(trainingDto);
        loggingService.logDebugController("created training: " + trainingDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @GetMapping("/trainee")
    public ResponseEntity<List<TrainingDto>> getTraineeTrainings(
            @RequestParam(name = "from-date", required = false) LocalDate fromDate,
            @RequestParam(name = "to-date", required = false) LocalDate toDate,
            @RequestParam(name = "trainer-id", required = false) Long trainerId,
            @RequestParam(name = "training-type-id", required = false) Long trainingTypeId)
    {
        String username = getUsername();
        TraineeTrainingsRequest request = TraineeTrainingsRequest.builder()
                .username(username)
                .fromDate(fromDate)
                .toDate(toDate)
                .trainerId(trainerId)
                .trainingTypeId(trainingTypeId)
                .build();

        List<TrainingDto> trainings = trainingService.getTraineeTrainings(request);
        loggingService.logDebugController("fetched it's trainee's trainings");

        return ResponseEntity.ok(trainings);
    }

    @Override
    @GetMapping("/trainer")
    public ResponseEntity<List<TrainingDto>> getTrainerTrainings(
            @RequestParam(name = "from-date", required = false) LocalDate fromDate,
            @RequestParam(name = "to-date", required = false) LocalDate toDate,
            @RequestParam(name = "trainee-id", required = false) Long trainerId)
    {
        String username = getUsername();
        TrainerTrainingsRequest request = TrainerTrainingsRequest.builder()
                .username(username)
                .fromDate(fromDate)
                .toDate(toDate)
                .traineeId(trainerId)
                .build();

        List<TrainingDto> trainings = trainingService.getTrainerTrainings(request);
        loggingService.logDebugController("fetched it's trainer's trainings");

        return ResponseEntity.ok(trainings);
    }

    private static String getUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
}

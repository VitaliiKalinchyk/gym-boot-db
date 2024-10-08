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
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody TrainingDto trainingDto) {
        String username = getUsername();
        trainingDto.getTrainee().getUser().setUsername(username);
        trainingService.create(trainingDto);
        loggingService.logDebugController("created training: " + trainingDto, username);
    }

    @Override
    @GetMapping("/trainee")
    public List<TrainingDto> getTraineeTrainings(
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
        loggingService.logDebugController("fetched its trainee's trainings");

        return trainings;
    }

    @Override
    @GetMapping("/trainer")
    public List<TrainingDto> getTrainerTrainings(
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
        loggingService.logDebugController("fetched its trainer's trainings");

        return trainings;
    }

    private static String getUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
}

package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.controller.TrainingController;
import epam.task.gymbootdb.dto.TraineeTrainingsRequest;
import epam.task.gymbootdb.dto.TrainerTrainingsRequest;
import epam.task.gymbootdb.dto.TrainingDto;
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

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createTraining(@Valid @RequestBody TrainingDto trainingDto) {
        trainingDto.getTrainee().getUser().setUsername(getUsername());

        trainingService.create(trainingDto);
    }

    @Override
    @GetMapping("/trainee")
    public List<TrainingDto> getTraineeTrainings(
            @RequestParam(name = "from-date", required = false) LocalDate fromDate,
            @RequestParam(name = "to-date", required = false) LocalDate toDate,
            @RequestParam(name = "trainer-id", required = false) Long trainerId,
            @RequestParam(name = "training-type-id", required = false) Long trainingTypeId)
    {
        TraineeTrainingsRequest request = TraineeTrainingsRequest.builder()
                .username(getUsername())
                .fromDate(fromDate)
                .toDate(toDate)
                .trainerId(trainerId)
                .trainingTypeId(trainingTypeId)
                .build();

        return trainingService.getTraineeTrainings(request);
    }

    @Override
    @GetMapping("/trainer")
    public List<TrainingDto> getTrainerTrainings(
            @RequestParam(name = "from-date", required = false) LocalDate fromDate,
            @RequestParam(name = "to-date", required = false) LocalDate toDate,
            @RequestParam(name = "trainee-id", required = false) Long trainerId)
    {
        TrainerTrainingsRequest request = TrainerTrainingsRequest.builder()
                .username(getUsername())
                .fromDate(fromDate)
                .toDate(toDate)
                .traineeId(trainerId)
                .build();

        return trainingService.getTrainerTrainings(request);
    }

    private static String getUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
}

package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.controller.AdminController;
import epam.task.gymbootdb.dto.*;
import epam.task.gymbootdb.service.*;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminControllerImpl implements AdminController {

    private final UserService userService;
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final LoggingService loggingService;

    @Override
    @PatchMapping("/users/status/{username}")
    public void changeActiveStatus(@PathVariable String username) {
        userService.changeStatus(username);
        loggingService.logDebugAdminController("changed active status for", username);
    }

    @Override
    @PutMapping("/trainees/{username}")
    public TraineeDto updateTrainee(@PathVariable String username, @Valid @RequestBody TraineeDto trainee) {
        trainee.getUser().setUsername(username);

        TraineeDto traineeDto = traineeService.update(trainee);
        loggingService.logDebugAdminController("updated profile for", username);

        return traineeDto;
    }

    @Override
    @DeleteMapping("/trainees/{username}")
    public void deleteTrainee(@PathVariable String username) {
        traineeService.deleteByUsername(username);
        loggingService.logDebugAdminController("deleted profile of", username);
    }

    @Override
    @GetMapping("/trainees/unassigned-trainers/{username}")
    public List<TrainerDto> getTrainersNotAssignedToTrainee(@PathVariable String username) {
        List<TrainerDto> trainersNotAssignedToTrainee = traineeService.getTrainersNotAssignedToTrainee(username);
        loggingService.logDebugAdminController("got unassigned trainers for", username);

        return trainersNotAssignedToTrainee;
    }

    @Override
    @PutMapping("/trainees/trainers/{username}")
    public void updateTraineeTrainers(@PathVariable String username, @RequestParam long trainerId) {
        traineeService.updateTraineeTrainers(username, trainerId);
        loggingService.logDebugAdminController("added new trainer for", username);
    }

    @Override
    @PutMapping("/trainers/{username}")
    public TrainerDto updateTrainer(@PathVariable String username, @RequestBody TrainerDto trainerDto) {
        trainerDto.getUser().setUsername(username);

        TrainerDto update = trainerService.update(trainerDto);
        loggingService.logDebugAdminController("updated profile for", username);

        return update;
    }

    @Override
    @PostMapping("/trainings/{username}")
    public void createTraining(@PathVariable String username, @Valid @RequestBody TrainingDto trainingDto) {
        trainingDto.getTrainee().getUser().setUsername(username);
        trainingService.create(trainingDto);
        loggingService.logDebugAdminController("created training for", username);
    }

    @Override
    @GetMapping("/trainings/trainee/{username}")
    public List<TrainingDto> getTraineeTrainings(
            @PathVariable String username,
            @RequestParam(name = "from-date", required = false) LocalDate fromDate,
            @RequestParam(name = "to-date", required = false) LocalDate toDate,
            @RequestParam(name = "trainer-id", required = false) Long trainerId,
            @RequestParam(name = "training-type-id", required = false) Long trainingTypeId)
    {
        TraineeTrainingsRequest request = TraineeTrainingsRequest.builder()
                .username(username)
                .fromDate(fromDate)
                .toDate(toDate)
                .trainerId(trainerId)
                .trainingTypeId(trainingTypeId)
                .build();

        List<TrainingDto> trainings = trainingService.getTraineeTrainings(request);
        loggingService.logDebugAdminController("fetched trainees trainings for", username);

        return trainings;
    }

    @Override
    @GetMapping("/trainings/trainer/{username}")
    public List<TrainingDto> getTrainerTrainings(
            @PathVariable String username,
            @RequestParam(name = "from-date", required = false) LocalDate fromDate,
            @RequestParam(name = "to-date", required = false) LocalDate toDate,
            @RequestParam(name = "trainee-id", required = false) Long trainerId)
    {
        TrainerTrainingsRequest request = TrainerTrainingsRequest.builder()
                .username(username)
                .fromDate(fromDate)
                .toDate(toDate)
                .traineeId(trainerId)
                .build();

        List<TrainingDto> trainings = trainingService.getTrainerTrainings(request);
        loggingService.logDebugAdminController("fetched trainers trainings for", username);

        return trainings;
    }
}

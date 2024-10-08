package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.controller.TraineeController;
import epam.task.gymbootdb.dto.TraineeDto;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.service.LoggingService;
import epam.task.gymbootdb.service.TraineeService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trainees")
@RequiredArgsConstructor
public class TraineeControllerImpl implements TraineeController {

    private final TraineeService traineeService;
    private final LoggingService loggingService;

    @Override
    @GetMapping("/{username}")
    public TraineeDto get(@PathVariable String username) {
        TraineeDto trainee = traineeService.getByUsername(username);
        loggingService.logDebugController("was fetched");

        return trainee;
    }

    @Override
    @GetMapping("/profile")
    public TraineeDto get() {
        TraineeDto trainee = traineeService.getByUsername(getUsername());
        loggingService.logDebugController("was fetched by itself");

        return trainee;
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserCredentials create(@Valid @RequestBody TraineeDto traineeDto) {
        UserCredentials profile = traineeService.createProfile(traineeDto);
        loggingService.logDebugController("was created as trainee", profile.getUsername());

        return profile;
    }

    @Override
    @PutMapping
    public TraineeDto update(@Valid @RequestBody TraineeDto trainee) {
        trainee.getUser().setUsername(getUsername());

        TraineeDto traineeDto = traineeService.update(trainee);
        loggingService.logDebugController("was updated");

        return traineeDto;
    }

    @Override
    @DeleteMapping
    public void delete() {
        traineeService.deleteByUsername(getUsername());
        loggingService.logDebugController("was deleted");
    }

    @Override
    @GetMapping("/unassigned-trainers")
    public List<TrainerDto> getTrainersNotAssignedToTrainee() {
        List<TrainerDto> trainersNotAssignedToTrainee = traineeService.getTrainersNotAssignedToTrainee(getUsername());
        loggingService.logDebugController("got unassigned trainers");

        return trainersNotAssignedToTrainee;
    }

    @Override
    @PutMapping("/trainers")
    public void updateTraineeTrainers(@RequestParam long trainerId) {
        traineeService.updateTraineeTrainers(getUsername(), trainerId);
        loggingService.logDebugController("added new trainer to its list");
    }

    private static String getUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
}

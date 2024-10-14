package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.controller.TraineeController;
import epam.task.gymbootdb.dto.TraineeDto;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.service.TraineeService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trainees")
@RequiredArgsConstructor
public class TraineeControllerImpl extends AbstractController implements TraineeController {

    private final TraineeService traineeService;

    @Override
    @GetMapping("/{username}")
    public TraineeDto getTraineeProfile(@PathVariable String username) {
        return traineeService.getByUsername(username);
    }

    @Override
    @GetMapping("/profile")
    public TraineeDto getTraineeProfile() {
        return traineeService.getByUsername(getUsername());
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserCredentials createTrainee(@Valid @RequestBody TraineeDto traineeDto) {
        return traineeService.createProfile(traineeDto);
    }

    @Override
    @PutMapping
    public TraineeDto updateTraineeProfile(@Valid @RequestBody TraineeDto trainee) {
        trainee.getUser().setUsername(getUsername());

        return traineeService.update(trainee);
    }

    @Override
    @DeleteMapping
    public void deleteTrainee() {
        traineeService.deleteByUsername(getUsername());
    }

    @Override
    @GetMapping("/unassigned-trainers")
    public List<TrainerDto> getTrainersNotAssignedToTrainee() {
        return traineeService.getTrainersNotAssignedToTrainee(getUsername());
    }

    @Override
    @PutMapping("/trainers")
    public void updateTraineeTrainers(@RequestParam long trainerId) {
        traineeService.updateTraineeTrainers(getUsername(), trainerId);
    }
}

package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.controller.AdminController;
import epam.task.gymbootdb.dto.TraineeDto;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.TrainingDto;
import epam.task.gymbootdb.dto.TraineeTrainingsRequest;
import epam.task.gymbootdb.dto.TrainerTrainingsRequest;
import epam.task.gymbootdb.service.UserService;
import epam.task.gymbootdb.service.TraineeService;
import epam.task.gymbootdb.service.TrainerService;
import epam.task.gymbootdb.service.TrainingService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
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

    @Override
    @PatchMapping("/users/status/{username}")
    public void changeActiveStatusForUserByAdmin(@PathVariable String username) {
        userService.changeStatus(username);
    }

    @Override
    @PutMapping("/trainees/{username}")
    public TraineeDto updateTraineeProfileByAdmin(@PathVariable String username,
                                                  @Valid @RequestBody TraineeDto trainee) {
        trainee.getUser().setUsername(username);

        return traineeService.update(trainee);
    }

    @Override
    @DeleteMapping("/trainees/{username}")
    public void deleteTraineeProfileByAdmin(@PathVariable String username) {
        traineeService.deleteByUsername(username);
    }

    @Override
    @GetMapping("/trainees/unassigned-trainers/{username}")
    public List<TrainerDto> getTrainersNotAssignedToTraineeByAdmin(@PathVariable String username) {
        return traineeService.getTrainersNotAssignedToTrainee(username);
    }

    @Override
    @PutMapping("/trainees/trainers/{username}")
    public void updateTraineeTrainersByAdmin(@PathVariable String username, @RequestParam long trainerId) {
        traineeService.updateTraineeTrainers(username, trainerId);
    }

    @Override
    @PutMapping("/trainers/{username}")
    public TrainerDto updateTrainerProfileByAdmin(@PathVariable String username,
                                                  @Valid @RequestBody TrainerDto trainerDto) {
        trainerDto.getUser().setUsername(username);

        return trainerService.update(trainerDto);
    }

    @Override
    @PostMapping("/trainings/{username}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createTrainingFoTraineeByAdmin(@PathVariable String username,
                                               @Valid @RequestBody TrainingDto trainingDto) {
        trainingDto.getTrainee().getUser().setUsername(username);
        trainingService.create(trainingDto);
    }

    @Override
    @GetMapping("/trainings/trainee/{username}")
    public List<TrainingDto> getTraineeTrainingsByAdmin(
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

        return trainingService.getTraineeTrainings(request);
    }

    @Override
    @GetMapping("/trainings/trainer/{username}")
    public List<TrainingDto> getTrainerTrainingsByAdmin(
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

        return trainingService.getTrainerTrainings(request);
    }
}

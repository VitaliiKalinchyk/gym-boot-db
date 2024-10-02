package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.controller.TraineeController;
import epam.task.gymbootdb.dto.TraineeDto;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.service.TraineeService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TraineeControllerImpl implements TraineeController {

    private final TraineeService traineeService;

    @Override
    public ResponseEntity<TraineeDto> get(long id) {
        TraineeDto trainee = traineeService.getById(id);

        return ResponseEntity.ok(trainee);
    }

    @Override
    public ResponseEntity<UserCredentials> create(TraineeDto traineeDto) {
        UserCredentials profile = traineeService.createProfile(traineeDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    @Override
    public ResponseEntity<TraineeDto> update(long id, TraineeDto trainee) {
        trainee.setId(id);
        TraineeDto traineeDto = traineeService.update(trainee);

        return ResponseEntity.ok(traineeDto);
    }

    @Override
    public ResponseEntity<Void> changeActiveStatus(long id) {
        traineeService.changeStatus(id);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> delete(long id) {
        traineeService.deleteById(id);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<TrainerDto>> getTrainersNotAssignedToTrainee(long id) {
        return ResponseEntity.ok(traineeService.getTrainersNotAssignedToTrainee(id));
    }

    @Override
    public ResponseEntity<Void> updateTraineeTrainers(long id, long trainerId) {
        traineeService.updateTraineeTrainers(id, trainerId);

        return ResponseEntity.ok().build();
    }
}

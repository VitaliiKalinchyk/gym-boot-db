package epam.task.gymbootdb.controller;

import epam.task.gymbootdb.dto.TraineeDto;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.service.TraineeService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trainees")
@RequiredArgsConstructor
public class TraineeController {

    private final TraineeService traineeService;

    @GetMapping("/{id}")
    public ResponseEntity<TraineeDto> get(@PathVariable long id) {
        TraineeDto trainee = traineeService.getById(id);

        return ResponseEntity.ok(trainee);
    }

    @PostMapping
    public ResponseEntity<UserCredentials> create(@Valid @RequestBody TraineeDto traineeDto) {
        UserCredentials profile = traineeService.createProfile(traineeDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TraineeDto> update(@PathVariable long id, @Valid @RequestBody TraineeDto trainee) {
        trainee.setId(id);
        TraineeDto traineeDto = traineeService.update(trainee);

        return ResponseEntity.ok(traineeDto);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeActiveStatus(@PathVariable long id) {
        traineeService.changeStatus(id);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        traineeService.deleteById(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/unassigned-trainers")
    public ResponseEntity<List<TrainerDto>> getTrainersNotAssignedToTrainee(@PathVariable long id) {
        return ResponseEntity.ok(traineeService.getTrainersNotAssignedToTrainee(id));
    }

    @PutMapping("/{id}/trainers")
    public ResponseEntity<Void> updateTraineeTrainers(@PathVariable long id, @RequestBody long trainerId) {
        traineeService.updateTraineeTrainers(id, trainerId);

        return ResponseEntity.ok().build();
    }
}
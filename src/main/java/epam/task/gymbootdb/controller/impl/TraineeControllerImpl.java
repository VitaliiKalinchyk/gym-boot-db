package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.controller.TraineeController;
import epam.task.gymbootdb.dto.TraineeDto;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.service.TraineeService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trainees")
@RequiredArgsConstructor
@Slf4j
public class TraineeControllerImpl implements TraineeController {

    private static final String TRANSACTION_ID = "transactionId";

    private final TraineeService traineeService;

    @Override
    @GetMapping("/{username}")
    public ResponseEntity<TraineeDto> get(@PathVariable String username) {
        TraineeDto trainee = traineeService.getByUsername(username);
        log.debug("Trainee (username = {}) was fetched. Controller layer. TransactionId: {}",
                 username, MDC.get(TRANSACTION_ID));

        return ResponseEntity.ok(trainee);
    }

    @Override
    @PostMapping
    public ResponseEntity<UserCredentials> create(@Valid @RequestBody TraineeDto traineeDto) {
        UserCredentials profile = traineeService.createProfile(traineeDto);
        log.debug("Trainee (username = {}) was created. Controller layer. TransactionId: {}",
                profile.getUsername(), MDC.get(TRANSACTION_ID));

        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    @Override
    @PutMapping
    public ResponseEntity<TraineeDto> update(@Valid @RequestBody TraineeDto trainee) {
        String username = getUsername();
        trainee.getUser().setUsername(username);

        TraineeDto traineeDto = traineeService.update(trainee);
        log.debug("Trainee (username = {}) was updated. Controller layer. TransactionId: {}",
                username, MDC.get(TRANSACTION_ID));

        return ResponseEntity.ok(traineeDto);
    }

    @Override
    @DeleteMapping
    public ResponseEntity<Void> delete() {
        String username = getUsername();
        traineeService.deleteByUsername(username);
        log.debug("Trainee (username = {}) was deleted. Controller layer. TransactionId: {}",
                username, MDC.get(TRANSACTION_ID));

        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping("/unassigned-trainers")
    public ResponseEntity<List<TrainerDto>> getTrainersNotAssignedToTrainee() {
        String username = getUsername();
        log.debug("Trainee (username = {}) got unassigned trainers. Controller layer. TransactionId: {}",
                username, MDC.get(TRANSACTION_ID));

        return ResponseEntity.ok(traineeService.getTrainersNotAssignedToTrainee(username));
    }

    @Override
    @PutMapping("/trainers")
    public ResponseEntity<Void> updateTraineeTrainers(@RequestParam long trainerId) {
        String username = getUsername();
        traineeService.updateTraineeTrainers(username, trainerId);
        log.debug("Trainee (username = {}) added new trainer to it's list (id = {}). " +
                "Controller layer. TransactionId: {}", username, trainerId, MDC.get(TRANSACTION_ID));

        return ResponseEntity.ok().build();
    }

    private static String getUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
}

package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.controller.TrainerController;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.service.TrainerService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trainers")
@Slf4j
public class TrainerControllerImpl implements TrainerController {

    private static final String TRANSACTION_ID = "transactionId";

    private final TrainerService trainerService;

    @Override
    @GetMapping("/{username}")
    public ResponseEntity<TrainerDto> get(@PathVariable String username) {
        TrainerDto trainerDto = trainerService.getByUsername(username);
        log.debug("Trainer (username = {}) was fetched. Controller layer. TransactionId: {}",
                username, MDC.get(TRANSACTION_ID));

        return ResponseEntity.ok(trainerDto);
    }

    @Override
    @GetMapping("/profile")
    public ResponseEntity<TrainerDto> get() {
        String username = getUsername();
        TrainerDto trainerDto = trainerService.getByUsername(username);
        log.debug("Trainer (username = {}) was fetched by itself. Controller layer. TransactionId: {}",
                username, MDC.get(TRANSACTION_ID));

        return ResponseEntity.ok(trainerDto);
    }

    @Override
    @PostMapping
    public ResponseEntity<UserCredentials> create(@Valid @RequestBody TrainerDto trainerDto) {
        UserCredentials profile = trainerService.createProfile(trainerDto);
        log.debug("Trainer (username = {}) was created. Controller layer. TransactionId: {}",
                profile.getUsername(), MDC.get(TRANSACTION_ID));

        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    @Override
    @PutMapping
    public ResponseEntity<TrainerDto> update(@Valid @RequestBody TrainerDto trainerDto){
        String username = getUsername();
        trainerDto.getUser().setUsername(username);

        TrainerDto update = trainerService.update(trainerDto);
        log.debug("Trainer (username = {}) was updated. Controller layer. TransactionId: {}",
                username, MDC.get(TRANSACTION_ID));

        return ResponseEntity.ok(update);
    }

    private static String getUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
}

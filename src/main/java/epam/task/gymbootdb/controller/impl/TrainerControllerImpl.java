package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.controller.TrainerController;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.service.LoggingService;
import epam.task.gymbootdb.service.TrainerService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trainers")
public class TrainerControllerImpl implements TrainerController {

    private final TrainerService trainerService;
    private final LoggingService loggingService;

    @Override
    @GetMapping("/{username}")
    public ResponseEntity<TrainerDto> get(@PathVariable String username) {
        TrainerDto trainerDto = trainerService.getByUsername(username);
        loggingService.logDebugController("was fetched");

        return ResponseEntity.ok(trainerDto);
    }

    @Override
    @GetMapping("/profile")
    public ResponseEntity<TrainerDto> get() {
        TrainerDto trainerDto = trainerService.getByUsername(getUsername());
        loggingService.logDebugController("was fetched by itself");

        return ResponseEntity.ok(trainerDto);
    }

    @Override
    @PostMapping
    public ResponseEntity<UserCredentials> create(@Valid @RequestBody TrainerDto trainerDto) {
        UserCredentials profile = trainerService.createProfile(trainerDto);
        loggingService.logDebugController("was created as trainer", profile.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    @Override
    @PutMapping
    public ResponseEntity<TrainerDto> update(@Valid @RequestBody TrainerDto trainerDto){
        trainerDto.getUser().setUsername(getUsername());

        TrainerDto update = trainerService.update(trainerDto);
        loggingService.logDebugController("was updated");

        return ResponseEntity.ok(update);
    }

    private static String getUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
}

package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.controller.TrainerController;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.service.LoggingService;
import epam.task.gymbootdb.service.TrainerService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
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
    public TrainerDto get(@PathVariable String username) {
        TrainerDto trainerDto = trainerService.getByUsername(username);
        loggingService.logDebugController("was fetched");

        return trainerDto;
    }

    @Override
    @GetMapping("/profile")
    public TrainerDto get() {
        TrainerDto trainerDto = trainerService.getByUsername(getUsername());
        loggingService.logDebugController("was fetched by itself");

        return trainerDto;
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserCredentials create(@Valid @RequestBody TrainerDto trainerDto) {
        UserCredentials profile = trainerService.createProfile(trainerDto);
        loggingService.logDebugController("was created as trainer", profile.getUsername());

        return profile;
    }

    @Override
    @PutMapping
    public TrainerDto update(@Valid @RequestBody TrainerDto trainerDto){
        trainerDto.getUser().setUsername(getUsername());

        TrainerDto update = trainerService.update(trainerDto);
        loggingService.logDebugController("was updated");

        return update;
    }

    private static String getUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
}

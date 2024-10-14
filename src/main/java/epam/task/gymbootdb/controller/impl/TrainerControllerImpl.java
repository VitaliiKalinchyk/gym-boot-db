package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.controller.TrainerController;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.service.TrainerService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trainers")
public class TrainerControllerImpl extends AbstractController implements TrainerController {

    private final TrainerService trainerService;

    @Override
    @GetMapping("/{username}")
    public TrainerDto getTrainerProfile(@PathVariable String username) {
        return trainerService.getByUsername(username);
    }

    @Override
    @GetMapping("/profile")
    public TrainerDto getTrainerProfile() {
        return trainerService.getByUsername(getUsername());
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserCredentials createTrainer(@Valid @RequestBody TrainerDto trainerDto) {
        return trainerService.createProfile(trainerDto);
    }

    @Override
    @PutMapping
    public TrainerDto updateTrainerProfile(@Valid @RequestBody TrainerDto trainerDto){
        trainerDto.getUser().setUsername(getUsername());

        return trainerService.update(trainerDto);
    }
}

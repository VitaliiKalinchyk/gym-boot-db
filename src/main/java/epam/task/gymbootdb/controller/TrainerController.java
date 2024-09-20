package epam.task.gymbootdb.controller;

import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.service.TrainerService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @GetMapping("/{id}")
    private ResponseEntity<TrainerDto> get(@PathVariable long id) {
        TrainerDto trainerDto = trainerService.getById(id);

        return ResponseEntity.ok(trainerDto);
    }

    @PostMapping
    public ResponseEntity<UserCredentials> create(@Valid @RequestBody TrainerDto trainerDto) {
        UserCredentials profile = trainerService.createProfile(trainerDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainerDto> update(@PathVariable long id, @Valid @RequestBody TrainerDto trainerDto){
        trainerDto.setId(id);
        TrainerDto update = trainerService.update(trainerDto);

        return ResponseEntity.ok(update);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeActiveStatus(@PathVariable long id){
        trainerService.setActiveStatus(id);

        return ResponseEntity.ok().build();
    }
}
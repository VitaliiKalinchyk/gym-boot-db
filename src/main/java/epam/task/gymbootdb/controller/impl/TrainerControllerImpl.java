package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.controller.TrainerController;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.service.TrainerService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TrainerControllerImpl implements TrainerController {

    private final TrainerService trainerService;

    @Override
    public ResponseEntity<TrainerDto> get(long id) {
        TrainerDto trainerDto = trainerService.getById(id);

        return ResponseEntity.ok(trainerDto);
    }

    @Override
    public ResponseEntity<UserCredentials> create(TrainerDto trainerDto) {
        UserCredentials profile = trainerService.createProfile(trainerDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    @Override
    public ResponseEntity<TrainerDto> update(long id,TrainerDto trainerDto){
        trainerDto.setId(id);
        TrainerDto update = trainerService.update(trainerDto);

        return ResponseEntity.ok(update);
    }
}

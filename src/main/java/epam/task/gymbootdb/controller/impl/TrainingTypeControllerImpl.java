package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.controller.TrainingTypeController;
import epam.task.gymbootdb.dto.TrainingTypeDto;
import epam.task.gymbootdb.service.TrainingTypeService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TrainingTypeControllerImpl implements TrainingTypeController {

    private final TrainingTypeService trainingTypeService;

    public ResponseEntity<List<TrainingTypeDto>> getAll() {
        return ResponseEntity.ok(trainingTypeService.getAll());
    }
}

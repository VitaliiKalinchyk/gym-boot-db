package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.controller.TrainingTypeController;
import epam.task.gymbootdb.dto.TrainingTypeDto;
import epam.task.gymbootdb.service.TrainingTypeService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/training-types")
@RequiredArgsConstructor
public class TrainingTypeControllerImpl implements TrainingTypeController {

    private final TrainingTypeService trainingTypeService;

    @Override
    @GetMapping
    public List<TrainingTypeDto> getAllTrainingTypes() {
        return trainingTypeService.getAll();
    }
}

package epam.task.gymbootdb.service;

import epam.task.gymbootdb.dto.TrainingTypeDto;

import java.util.List;

public interface TrainingTypeService {
    List<TrainingTypeDto> getAll();
}

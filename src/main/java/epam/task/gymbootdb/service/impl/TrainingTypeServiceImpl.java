package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.TrainingTypeDto;
import epam.task.gymbootdb.dto.mapper.TrainingTypeMapper;
import epam.task.gymbootdb.repository.TrainingTypeRepository;
import epam.task.gymbootdb.service.TrainingTypeService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingTypeMapper trainingTypeMapper;

    @Override
    public List<TrainingTypeDto> getAll() {
        return trainingTypeMapper.toDtoList(trainingTypeRepository.findAll());
    }
}

package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.TrainingTypeDto;
import epam.task.gymbootdb.dto.mapper.TrainingTypeMapper;
import epam.task.gymbootdb.entity.TrainingType;
import epam.task.gymbootdb.repository.TrainingTypeRepository;

import epam.task.gymbootdb.service.LoggingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceImplTest {
    @InjectMocks
    private TrainingTypeServiceImpl trainingTypeService;
    @Mock
    private TrainingTypeRepository trainingTypeRepository;
    @Mock
    private  TrainingTypeMapper trainingTypeMapper;
    @Mock
    private LoggingService loggingService;

    @Test
    void testGetAll() {
        List<TrainingType> trainingTypes= List.of();
        List<TrainingTypeDto> trainingTypeDtos= List.of(new TrainingTypeDto());

        when(trainingTypeRepository.findAll()).thenReturn(trainingTypes);
        when(trainingTypeMapper.toDtoList(trainingTypes)).thenReturn(trainingTypeDtos);

        List<TrainingTypeDto> all = trainingTypeService.getAll();

        assertEquals(1, all.size());
        verify(loggingService).logDebugService(anyString());
    }
}

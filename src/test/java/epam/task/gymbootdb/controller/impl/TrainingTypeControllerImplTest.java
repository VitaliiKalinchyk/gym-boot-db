package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.dto.TrainingTypeDto;
import epam.task.gymbootdb.service.LoggingService;
import epam.task.gymbootdb.service.TrainingTypeService;

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
class TrainingTypeControllerImplTest {

    @InjectMocks
    TrainingTypeControllerImpl controller;

    @Mock
    TrainingTypeService trainingTypeService;
    @Mock
    LoggingService loggingService;

    @Test
    void testGetAll() {
        TrainingTypeDto yoga = new TrainingTypeDto(1, "YOGA");
        List<TrainingTypeDto> trainingTypes = List.of(yoga);

        when(trainingTypeService.getAll()).thenReturn(trainingTypes);

        List<TrainingTypeDto> response = controller.getAll();

        assertEquals(1, response.size());
        assertEquals(yoga, response.getFirst());
        verify(loggingService).logDebugController(anyString());
    }
}

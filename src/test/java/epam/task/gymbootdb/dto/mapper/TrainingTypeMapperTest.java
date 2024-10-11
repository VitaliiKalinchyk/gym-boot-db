package epam.task.gymbootdb.dto.mapper;

import epam.task.gymbootdb.dto.TrainingTypeDto;
import epam.task.gymbootdb.entity.TrainingType;

import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrainingTypeMapperTest {

    private final TrainingTypeMapper mapper = Mappers.getMapper(TrainingTypeMapper.class);

    @Test
    void toDtoList() {
        TrainingType trainingType1 = new TrainingType(1, "YOGA");
        TrainingType trainingType2 = new TrainingType(2, "ZUMBA");
        List<TrainingType> trainingTypes= List.of(trainingType1, trainingType2);

        List<TrainingTypeDto> dtoList = mapper.toDtoList(trainingTypes);

        assertEquals(trainingType1.getId(), dtoList.getFirst().getId());
        assertEquals(trainingType1.getName(), dtoList.getFirst().getName());
        assertEquals(trainingType2.getId(), dtoList.getLast().getId());
        assertEquals(trainingType2.getName(), dtoList.getLast().getName());
    }

    @Test
    void toDtoListWithNullList() {
        List<TrainingTypeDto> dtoList = mapper.toDtoList(null);

        assertNull(dtoList);
    }
}

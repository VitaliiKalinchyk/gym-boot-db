package epam.task.gymbootdb.dto.mapper;

import epam.task.gymbootdb.dto.TrainingTypeDto;
import epam.task.gymbootdb.entity.TrainingType;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrainingTypeMapper {
    List<TrainingTypeDto> toDtoList(List<TrainingType> trainingTypes);
}

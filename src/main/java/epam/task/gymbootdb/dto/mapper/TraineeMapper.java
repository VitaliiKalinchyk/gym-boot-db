package epam.task.gymbootdb.dto.mapper;

import epam.task.gymbootdb.dto.TraineeDto;
import epam.task.gymbootdb.entity.Trainee;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TraineeMapper {

    @Mapping(target = "trainers", ignore = true)
    TraineeDto toDto(Trainee trainee);

    @Mapping(source = "user.active", target = "user.isActive")
    @Mapping(target = "user.password", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    Trainee toEntity(TraineeDto trainee);

    List<TraineeDto> toDtoList(List<Trainee> trainers);
}
package epam.task.gymbootdb.dto.mapper;

import epam.task.gymbootdb.dto.TraineeCreateOrUpdateRequest;
import epam.task.gymbootdb.dto.TraineeResponse;
import epam.task.gymbootdb.entity.Trainee;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TraineeMapper {
    TraineeResponse toDto(Trainee trainee);

    @Mapping(target = "trainers", ignore = true)
    Trainee toEntity(TraineeCreateOrUpdateRequest trainee);

    List<TraineeResponse> toDtoList(List<Trainee> trainees);
}
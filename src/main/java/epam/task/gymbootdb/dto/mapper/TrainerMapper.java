package epam.task.gymbootdb.dto.mapper;

import epam.task.gymbootdb.dto.TrainerCreateOrUpdateRequest;
import epam.task.gymbootdb.dto.TrainerResponse;
import epam.task.gymbootdb.entity.Trainer;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrainerMapper {

    TrainerResponse toDto(Trainer trainer);

    @Mapping(target = "trainees", ignore = true)
    Trainer toEntity(TrainerCreateOrUpdateRequest trainer);

    List<TrainerResponse> toDtoList(List<Trainer> trainers);
}
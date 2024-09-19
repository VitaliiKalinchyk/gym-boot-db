package epam.task.gymbootdb.dto.mapper;

import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.entity.Trainer;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrainerMapper {

    @Mapping(target = "trainees", ignore = true)
    TrainerDto toDto(Trainer trainer);

    @Mapping(source = "user.active", target = "user.isActive")
    @Mapping(target = "user.password", ignore = true)
    @Mapping(target = "trainees", ignore = true)
    Trainer toEntity(TrainerDto trainer);

    List<TrainerDto> toDtoList(List<Trainer> trainers);
}
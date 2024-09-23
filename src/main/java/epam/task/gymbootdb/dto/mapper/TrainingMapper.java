    package epam.task.gymbootdb.dto.mapper;

    import epam.task.gymbootdb.dto.TrainingDto;
    import epam.task.gymbootdb.entity.Training;

    import org.mapstruct.Mapper;
    import org.mapstruct.Mapping;

    import java.util.List;

    @Mapper(componentModel = "spring")
    public interface TrainingMapper {

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "trainer.user", ignore = true)
        @Mapping(target = "trainer.trainees", ignore = true)
        @Mapping(target = "trainee.user", ignore = true)
        @Mapping(target = "trainee.trainers", ignore = true)
        Training toEntity(TrainingDto trainingDto);

        @Mapping(target = "trainer.trainees", ignore = true)
        @Mapping(target = "trainee.trainers", ignore = true)
        TrainingDto toDto(Training training);

        List<TrainingDto> toDtoList(List<Training> trainings);
    }

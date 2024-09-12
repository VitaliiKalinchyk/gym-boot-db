    package epam.task.gymbootdb.dto.mapper;

    import epam.task.gymbootdb.dto.TrainingCreateRequest;
    import epam.task.gymbootdb.dto.TrainingResponse;
    import epam.task.gymbootdb.entity.Training;

    import org.mapstruct.Mapper;
    import org.mapstruct.Mapping;

    import java.util.List;

    @Mapper(componentModel = "spring")
    public interface TrainingMapper {

        @Mapping(target = "id", ignore = true)
        @Mapping(source = "trainerId", target = "trainer.id")
        @Mapping(source = "traineeId", target = "trainee.id")
        @Mapping(source = "trainingTypeId", target = "trainingType.id")
        Training toEntity(TrainingCreateRequest request);

        TrainingResponse toDto(Training training);

        List<TrainingResponse> toDtoList(List<Training> trainings);
    }
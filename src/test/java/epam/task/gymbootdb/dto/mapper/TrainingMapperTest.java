package epam.task.gymbootdb.dto.mapper;

import epam.task.gymbootdb.dto.TrainingDto;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.TraineeDto;
import epam.task.gymbootdb.dto.TrainingTypeDto;
import epam.task.gymbootdb.entity.Training;
import epam.task.gymbootdb.entity.Trainer;
import epam.task.gymbootdb.entity.Trainee;
import epam.task.gymbootdb.entity.TrainingType;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;

class TrainingMapperTest {

    private final TrainingMapper mapper = Mappers.getMapper(TrainingMapper.class);

    @Test
    void testToDto() {
        TrainingDto trainingDto = mapper.toDto(getTraining1());

        assertTraining(getTraining1(), trainingDto);
    }

    @Test
    void testToEntity() {
        Training training = mapper.toEntity(getTrainingDto());

        assertTraining(training, getTrainingDto());
    }

    @Test
    void testToDtoList() {
        List<TrainingDto> trainingDtos = mapper.toDtoList(List.of(getTraining1(), getTraining2()));

        assertTraining(getTraining1(), trainingDtos.get(0));
        assertTraining(getTraining2(), trainingDtos.get(1));
    }

    private static void assertTraining(Training training, TrainingDto trainingDto) {
        assertEquals(training.getId(), trainingDto.getId());
        assertEquals(training.getName(), trainingDto.getName());
        assertEquals(training.getDate(), trainingDto.getDate());
        assertEquals(training.getDuration(), trainingDto.getDuration());
        assertEquals(training.getTrainer().getId(), trainingDto.getTrainer().getId());
        assertEquals(training.getTrainee().getId(), trainingDto.getTrainee().getId());
        assertEquals(training.getTrainingType().getId(), trainingDto.getTrainingType().getId());
    }

    private static Training getTraining1() {
        return Training.builder()
                .id(1L)
                .name("Morning Yoga")
                .date(LocalDate.of(2024, 12, 12))
                .duration(60)
                .trainer(Trainer.builder().id(1L).build())
                .trainee(Trainee.builder().id(1L).build())
                .trainingType(TrainingType.builder().id(1L).build())
                .build();
    }

    private static Training getTraining2() {
        return Training.builder()
                .id(2L)
                .name("Evening Boxing")
                .date(LocalDate.of(2024, 12, 13))
                .duration(45)
                .trainer(Trainer.builder().id(2L).build())
                .trainee(Trainee.builder().id(2L).build())
                .trainingType(TrainingType.builder().id(2L).build())
                .build();
    }

    private static TrainingDto getTrainingDto() {
        return TrainingDto.builder()
                .name("Morning Yoga")
                .date(LocalDate.of(2024, 12, 12))
                .duration(60)
                .trainer(TrainerDto.builder().id(1L).build())
                .trainee(TraineeDto.builder().id(2L).build())
                .trainingType(TrainingTypeDto.builder().id(3L).build())
                .build();
    }
}

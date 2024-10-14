package epam.task.gymbootdb.dto.mapper;

import epam.task.gymbootdb.dto.*;
import epam.task.gymbootdb.entity.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;

class TrainingMapperTest {

    private final TrainingMapper mapper = Mappers.getMapper(TrainingMapper.class);

    @Test
    void toDto() {
        TrainingDto trainingDto = mapper.toDto(getTraining1());

        assertTraining(getTraining1(), trainingDto);
    }

    @Test
    void toDtoNullValue() {
        TrainingDto trainingDto = mapper.toDto(null);

        assertNull(trainingDto);
    }

    @Test
    void toEntity() {
        Training training = mapper.toEntity(getTrainingDto());

        assertTraining(training, getTrainingDto());
    }

    @Test
    void toEntityNullValue() {
        Training training = mapper.toEntity(null);

        assertNull(training);
    }

    @Test
    void toDtoList() {
        List<TrainingDto> trainingDtos = mapper.toDtoList(List.of(getTraining1(), getTraining2()));

        assertTraining(getTraining1(), trainingDtos.get(0));
        assertTraining(getTraining2(), trainingDtos.get(1));
    }

    @Test
    void toDtoListNullList() {
        List<TrainingDto> trainingDtos = mapper.toDtoList(null);

        assertNull(trainingDtos);
    }

    private void assertTraining(Training training, TrainingDto trainingDto) {
        assertEquals(training.getId(), trainingDto.getId());
        assertEquals(training.getName(), trainingDto.getName());
        assertEquals(training.getDate(), trainingDto.getDate());
        assertEquals(training.getDuration(), trainingDto.getDuration());
        assertEquals(training.getTrainer().getId(), trainingDto.getTrainer().getId());
        assertEquals(training.getTrainee().getId(), trainingDto.getTrainee().getId());
        assertEquals(training.getTrainingType().getId(), trainingDto.getTrainingType().getId());
    }

    private Training getTraining1() {
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

    private Training getTraining2() {
        return Training.builder()
                .id(2L)
                .name("Evening Boxing")
                .date(LocalDate.of(2024, 12, 13))
                .duration(45)
                .trainer(getTrainer())
                .trainee(getTrainee())
                .trainingType(getTrainingType())
                .build();
    }

    private Trainee getTrainee() {
        return Trainee.builder()
                .id(2L)
                .user(getUser())
                .birthday(LocalDate.now())
                .address("address")
                .build();
    }

    private Trainer getTrainer() {
        return Trainer.builder()
                .id(2L)
                .user(getUser())
                .trainingType(getTrainingType())
                .build();
    }

    private User getUser() {
        return User.builder()
                .id(1)
                .firstName("Joe")
                .lastName("Smith")
                .username("joe.smith")
                .isActive(true)
                .build();
    }

    private TrainingType getTrainingType() {
        return new TrainingType(1, "Yoga");
    }

    private TrainingDto getTrainingDto() {
        return TrainingDto.builder()
                .name("Morning Yoga")
                .date(LocalDate.of(2024, 12, 12))
                .duration(60)
                .trainer(getTrainerDto())
                .trainee(getTraineeDto())
                .trainingType(getTrainingTypeDto())
                .build();
    }

    private TraineeDto getTraineeDto() {
        return TraineeDto.builder()
                .id(2L)
                .user(getUserDto())
                .birthday(LocalDate.now())
                .address("address")
                .build();
    }

    private TrainerDto getTrainerDto() {
        return TrainerDto.builder()
                .id(1L)
                .user(getUserDto())
                .trainingType(getTrainingTypeDto())
                .build();
    }

    private TrainingTypeDto getTrainingTypeDto() {
        return new TrainingTypeDto(1, "Yoga");
    }
    
    private UserDto getUserDto() {
        return UserDto.builder()
                .firstName("John")
                .lastName("Doe")
                .username("john.doe")
                .id(1L)
                .active(true)
                .build();
    }
}

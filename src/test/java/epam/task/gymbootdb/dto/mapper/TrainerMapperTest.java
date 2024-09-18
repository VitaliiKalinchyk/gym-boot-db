package epam.task.gymbootdb.dto.mapper;

import epam.task.gymbootdb.dto.TraineeDto;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.TrainingTypeDto;
import epam.task.gymbootdb.dto.UserDto;
import epam.task.gymbootdb.entity.Trainee;
import epam.task.gymbootdb.entity.Trainer;
import epam.task.gymbootdb.entity.TrainingType;
import epam.task.gymbootdb.entity.User;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.mapstruct.factory.Mappers;

import java.util.List;

public class TrainerMapperTest {

    private final TrainerMapper mapper = Mappers.getMapper(TrainerMapper.class);

    @Test
    public void testToDto() {
        Trainer trainer = getTrainer1(getUser1());

        TrainerDto dto = mapper.toDto(trainer);

        assertTrainerDto(trainer, dto);
        assertNull(dto.getTrainees());
    }

    @Test
    public void testToEntity() {
        TrainerDto dto = getDto(getUserDto());

        Trainer trainer = mapper.toEntity(dto);

        assertTrainerDto(trainer, dto);
    }

    @Test
    public void testToEntityWithTrainees() {
        TrainerDto dto = getDto(getUserDto());
        TraineeDto trainee1 = TraineeDto.builder().id(1).build();
        TraineeDto trainee2 = TraineeDto.builder().id(2).build();
        dto.setTrainees(List.of(trainee1, trainee2));

        List<Trainee> trainees = mapper.toEntity(dto).getTrainees();

        assertEquals(2, trainees.size());
        assertEquals(trainee1.getId(), trainees.get(0).getId());
        assertEquals(trainee2.getId(), trainees.get(1).getId());
    }

    @Test
    public void testToDtoList() {
        Trainer trainer1 = getTrainer1(getUser1());
        Trainer trainer2 = getTrainer2(getUser2());
        List<Trainer> trainers = List.of(trainer1, trainer2);

        List<TrainerDto> trainerDtos = mapper.toDtoList(trainers);

        assertTrainerDto(trainer1, trainerDtos.get(0));
        assertTrainerDto(trainer2, trainerDtos.get(1));
    }

    private static void assertTrainerDto(Trainer trainer, TrainerDto dto) {
        assertEquals(trainer.getId(), dto.getId());
        assertEquals(trainer.getTrainingType().getId(), dto.getTrainingType().getId());
        assertEquals(trainer.getTrainingType().getName(), dto.getTrainingType().getName());
        assertEquals(trainer.getUser().getId(), dto.getUser().getId());
        assertEquals(trainer.getUser().getFirstName(), dto.getUser().getFirstName());
        assertEquals(trainer.getUser().getLastName(), dto.getUser().getLastName());
        assertEquals(trainer.getUser().getUsername(), dto.getUser().getUsername());
        assertEquals(trainer.getUser().isActive(), dto.getUser().isActive());
    }

    private static User getUser1() {
        return User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .username("John.Doe")
                .isActive(true)
                .build();
    }

    private static User getUser2() {
        return User.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Doe")
                .username("Jane.Doe")
                .isActive(true)
                .build();
    }

    private static Trainer getTrainer1(User user) {
        return Trainer.builder()
                .id(1L)
                .user(user)
                .trainingType(new TrainingType(1, "YOGA"))
                .trainees(List.of(new Trainee()))
                .build();
    }

    private static Trainer getTrainer2(User user) {
        return Trainer.builder()
                .id(2L)
                .user(user)
                .trainingType(new TrainingType(2, "BOXING"))
                .build();

    }

    private static TrainerDto getDto(UserDto user) {
        return TrainerDto.builder()
                .id(1L)
                .user(user)
                .trainingType(new TrainingTypeDto(1, "YOGA"))
                .build();

    }

    private static UserDto getUserDto() {
        return UserDto.builder()
                .username("John.Doe")
                .firstName("John")
                .lastName("Doe")
                .active(true)
                .build();
    }
}
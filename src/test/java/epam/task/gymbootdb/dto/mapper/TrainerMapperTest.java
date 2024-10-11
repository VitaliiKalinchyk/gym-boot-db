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

class TrainerMapperTest {

    private final TrainerMapper mapper = Mappers.getMapper(TrainerMapper.class);

    @Test
    void toDto() {
        TrainerDto dto = mapper.toDto(getTrainer1());

        assertTrainer(getTrainer1(), dto);
        assertNull(dto.getTrainees());
    }

    @Test
    void toDtoNullUser() {
        Trainer trainer = getTrainer1();
        trainer.setUser(null);

        TrainerDto dto = mapper.toDto(trainer);

        assertNull(dto.getUser());
    }

    @Test
    void toDtoNullTrainingType() {
        Trainer trainer = getTrainer1();
        trainer.setTrainingType(null);

        TrainerDto dto = mapper.toDto(trainer);

        assertNull(dto.getTrainingType());
    }

    @Test
    void toDtoNullValue() {
        TrainerDto dto = mapper.toDto(null);

        assertNull(dto);
    }

    @Test
    void toEntity() {
        Trainer trainer = mapper.toEntity(getDto());

        assertTrainer(trainer, getDto());
        assertNull(trainer.getTrainees());
    }

    @Test
    void toEntityNullUser() {
        TrainerDto dto = getDto();
        dto.setUser(null);

        Trainer trainer = mapper.toEntity(dto);

        assertNull(trainer.getUser());
    }

    @Test
    void toEntityNullTrainingType() {
        TrainerDto dto = getDto();
        dto.setTrainingType(null);

        Trainer trainer = mapper.toEntity(dto);

        assertNull(trainer.getTrainingType());
    }

    @Test
    void toEntityNullValue() {
        Trainer trainer = mapper.toEntity(null);

        assertNull(trainer);
    }

    @Test
    void toDtoList() {
        List<TrainerDto> trainerDtos = mapper.toDtoList(List.of(getTrainer1(), getTrainer2()));

        assertTrainer(getTrainer1(), trainerDtos.getFirst());
        assertTrainer(getTrainer2(), trainerDtos.getLast());
    }


    @Test
    void toDtoListNullList() {
        List<TrainerDto> trainerDtos = mapper.toDtoList(null);

        assertNull(trainerDtos);
    }

    private static void assertTrainer(Trainer trainer, TrainerDto dto) {
        assertEquals(trainer.getId(), dto.getId());
        assertEquals(trainer.getTrainingType().getId(), dto.getTrainingType().getId());
        assertEquals(trainer.getTrainingType().getName(), dto.getTrainingType().getName());
        assertEquals(trainer.getUser().getId(), dto.getUser().getId());
        assertEquals(trainer.getUser().getFirstName(), dto.getUser().getFirstName());
        assertEquals(trainer.getUser().getLastName(), dto.getUser().getLastName());
        assertEquals(trainer.getUser().getUsername(), dto.getUser().getUsername());
        assertEquals(trainer.getUser().isActive(), dto.getUser().isActive());
    }

    private static Trainer getTrainer1() {
        return Trainer.builder()
                .id(1L)
                .user(User.builder()
                        .id(1L)
                        .firstName("John")
                        .lastName("Doe")
                        .username("John.Doe")
                        .isActive(true)
                        .build())
                .trainingType(new TrainingType(1, "YOGA"))
                .trainees(List.of(new Trainee()))
                .build();
    }

    private static Trainer getTrainer2() {
        return Trainer.builder()
                .id(2L)
                .user(User.builder()
                        .id(2L)
                        .firstName("Jane")
                        .lastName("Doe")
                        .username("Jane.Doe")
                        .isActive(true)
                        .build())
                .trainingType(new TrainingType(2, "BOXING"))
                .build();

    }

    private static TrainerDto getDto() {
        return TrainerDto.builder()
                .id(1L)
                .user(UserDto.builder()
                        .username("John.Doe")
                        .firstName("John")
                        .lastName("Doe")
                        .active(true)
                        .build())
                .trainingType(new TrainingTypeDto(1, "YOGA"))
                .trainees(List.of(new TraineeDto()))
                .build();
    }
}

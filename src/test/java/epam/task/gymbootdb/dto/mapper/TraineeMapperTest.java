package epam.task.gymbootdb.dto.mapper;

import epam.task.gymbootdb.dto.TraineeDto;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserDto;
import epam.task.gymbootdb.entity.Trainee;
import epam.task.gymbootdb.entity.Trainer;
import epam.task.gymbootdb.entity.User;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;

public class TraineeMapperTest {

    private final TraineeMapper mapper = Mappers.getMapper(TraineeMapper.class);

    @Test
    public void testToDto() {
        Trainee trainee = getTrainee1(getUser1());

        TraineeDto dto = mapper.toDto(trainee);

        assertTraineeDto(trainee, dto);
        assertNull(dto.getTrainers());
    }

    @Test
    public void testToEntity() {
        TraineeDto dto = getDto(getUserDto());

        Trainee trainee = mapper.toEntity(dto);

        assertTraineeDto(trainee, dto);
    }

    @Test
    public void testToEntityWithTrainers() {
        TraineeDto dto = getDto(getUserDto());
        TrainerDto trainer1 = TrainerDto.builder().id(1).build();
        TrainerDto trainer2 = TrainerDto.builder().id(2).build();
        dto.setTrainers(List.of(trainer1, trainer2));

        List<Trainer> trainers = mapper.toEntity(dto).getTrainers();

        assertEquals(2, trainers.size());
        assertEquals(trainer1.getId(), trainers.get(0).getId());
        assertEquals(trainer2.getId(), trainers.get(1).getId());
    }

    private static void assertTraineeDto(Trainee trainee, TraineeDto dto) {
        assertEquals(trainee.getId(), dto.getId());
        assertEquals(trainee.getAddress(), dto.getAddress());
        assertEquals(trainee.getBirthday(), dto.getBirthday());
        assertEquals(trainee.getUser().getId(), dto.getUser().getId());
        assertEquals(trainee.getUser().getFirstName(), dto.getUser().getFirstName());
        assertEquals(trainee.getUser().getLastName(), dto.getUser().getLastName());
        assertEquals(trainee.getUser().getUsername(), dto.getUser().getUsername());
        assertEquals(trainee.getUser().isActive(), dto.getUser().isActive());
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

    private static Trainee getTrainee1(User user) {
        return Trainee.builder()
                .id(1L)
                .user(user)
                .birthday(LocalDate.of(1990, 1, 1))
                .address("Test Address")
                .trainers(List.of(new Trainer()))
                .build();
    }

    private static TraineeDto getDto(UserDto user) {
        return TraineeDto.builder()
                .id(1L)
                .user(user)
                .birthday(LocalDate.of(1990, 1, 1))
                .address("Test Address")
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
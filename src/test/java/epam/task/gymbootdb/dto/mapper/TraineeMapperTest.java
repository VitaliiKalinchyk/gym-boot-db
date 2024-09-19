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
        TraineeDto dto = mapper.toDto(getTrainee1());

        assertTrainee(getTrainee1(), dto);
        assertNull(dto.getTrainers());
    }

    @Test
    public void testToEntity() {
        Trainee trainee = mapper.toEntity(getDto());

        assertTrainee(trainee, getDto());
        assertNull(trainee.getTrainers());
    }

    @Test
    public void testToDtoList() {
        List<TraineeDto> traineeDtos = mapper.toDtoList(List.of(getTrainee1(), getTrainee2()));

        assertTrainee(getTrainee1(), traineeDtos.get(0));
        assertTrainee(getTrainee2(), traineeDtos.get(1));
    }

    private static void assertTrainee(Trainee trainee, TraineeDto dto) {
        assertEquals(trainee.getId(), dto.getId());
        assertEquals(trainee.getAddress(), dto.getAddress());
        assertEquals(trainee.getBirthday(), dto.getBirthday());
        assertEquals(trainee.getUser().getId(), dto.getUser().getId());
        assertEquals(trainee.getUser().getFirstName(), dto.getUser().getFirstName());
        assertEquals(trainee.getUser().getLastName(), dto.getUser().getLastName());
        assertEquals(trainee.getUser().getUsername(), dto.getUser().getUsername());
        assertEquals(trainee.getUser().isActive(), dto.getUser().isActive());
    }

    private static Trainee getTrainee1() {
        return Trainee.builder()
                .id(1L)
                .user(User.builder()
                        .id(1L)
                        .firstName("John")
                        .lastName("Doe")
                        .username("John.Doe")
                        .isActive(true)
                        .build())
                .birthday(LocalDate.of(1990, 1, 1))
                .address("Test Address")
                .trainers(List.of(new Trainer()))
                .build();
    }

    private static Trainee getTrainee2() {
        return Trainee.builder()
                .id(2L)
                .user(User.builder()
                        .id(2L)
                        .firstName("Jane")
                        .lastName("Smith")
                        .username("Jane.Smith")
                        .isActive(false)
                        .build())
                .birthday(LocalDate.of(2000, 10, 19))
                .address("Test Address 2")
                .build();
    }

    private static TraineeDto getDto() {
        return TraineeDto.builder()
                .id(1L)
                .user(UserDto.builder()
                        .username("John.Doe")
                        .firstName("John")
                        .lastName("Doe")
                        .active(true)
                        .build())
                .birthday(LocalDate.of(1990, 1, 1))
                .address("Test Address")
                .trainers(List.of(new TrainerDto()))
                .build();
    }
}
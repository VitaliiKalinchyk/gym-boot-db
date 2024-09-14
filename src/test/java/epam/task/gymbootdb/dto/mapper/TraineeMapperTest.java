package epam.task.gymbootdb.dto.mapper;

import epam.task.gymbootdb.dto.*;
import epam.task.gymbootdb.entity.Trainee;
import epam.task.gymbootdb.entity.Trainer;
import epam.task.gymbootdb.entity.User;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class TraineeMapperTest {

    private final TraineeMapper mapper = Mappers.getMapper(TraineeMapper.class);

    @Test
    public void testToDto() {
        Trainee trainee = getTrainee1(getUser1());

        TraineeResponse response = mapper.toDto(trainee);

        assertTraineeResponse(trainee, response);
    }
    @Test
    public void testToDtoWithTrainers() {
        Trainee trainee = getTrainee1(getUser1());
        Trainer trainer1 = Trainer.builder().id(1).build();
        Trainer trainer2 = Trainer.builder().id(2).build();
        trainee.setTrainers(Set.of(trainer1, trainer2));

        Set<TrainerResponse> trainerResponses = mapper.toDtoWithTrainers(trainee).getTrainers();

        assertEquals(2, trainerResponses.size());
    }

    @Test
    public void testToEntity() {
        TraineeCreateOrUpdateRequest request = getRequest(getUserCreateOrUpdateRequest());

        Trainee trainee = mapper.toEntity(request);

        assertEquals(request.getAddress(), trainee.getAddress());
        assertEquals(request.getBirthday(), trainee.getBirthday());
        assertEquals(request.getUser().getFirstName(), trainee.getUser().getFirstName());
        assertEquals(request.getUser().getLastName(), trainee.getUser().getLastName());
    }

    @Test
    public void testToDtoList() {
        Trainee trainee1 = getTrainee1(getUser1());
        Trainee trainee2 = getTrainee2(getUser2());
        List<Trainee> trainees = List.of(trainee1, trainee2);

        List<TraineeResponse> traineeResponses = mapper.toDtoList(trainees);

        assertTraineeResponse(trainee1, traineeResponses.get(0));
        assertTraineeResponse(trainee2, traineeResponses.get(1));
    }

    private static void assertTraineeResponse(Trainee trainee, TraineeResponse response) {
        assertEquals(trainee.getId(), response.getId());
        assertEquals(trainee.getAddress(), response.getAddress());
        assertEquals(trainee.getBirthday(), response.getBirthday());
        assertEquals(trainee.getUser().getId(), response.getUser().getId());
        assertEquals(trainee.getUser().getFirstName(), response.getUser().getFirstName());
        assertEquals(trainee.getUser().getLastName(), response.getUser().getLastName());
        assertEquals(trainee.getUser().getUsername(), response.getUser().getUsername());
        assertEquals(trainee.getUser().isActive(), response.getUser().isActive());
    }

    private static User getUser1() {
        return User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .username("John.Doe")
                .isActive(true).build();
    }

    private static User getUser2() {
        return User.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Doe")
                .username("Jane.Doe")
                .isActive(true).build();
    }

    private static Trainee getTrainee1(User user) {
        return Trainee.builder()
                .id(1L)
                .user(user)
                .birthday(LocalDate.of(1990, 1, 1))
                .address("Test Address").build();
    }

    private static Trainee getTrainee2(User user) {
        return Trainee.builder()
                .id(2L)
                .user(user)
                .birthday(LocalDate.of(1995, 5, 5))
                .address("Test Address 2").build();
    }

    private static TraineeCreateOrUpdateRequest getRequest(UserCreateOrUpdateRequest user) {
        return TraineeCreateOrUpdateRequest.builder()
                .id(1L)
                .user(user)
                .birthday(LocalDate.of(1990, 1, 1))
                .address("Test Address").build();
    }

    private static UserCreateOrUpdateRequest getUserCreateOrUpdateRequest() {
        return UserCreateOrUpdateRequest.builder().firstName("John").lastName("Doe").build();
    }
}
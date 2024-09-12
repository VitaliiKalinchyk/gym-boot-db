package epam.task.gymbootdb.dto.mapper;

import epam.task.gymbootdb.dto.TrainerCreateOrUpdateRequest;
import epam.task.gymbootdb.dto.TrainerResponse;
import epam.task.gymbootdb.dto.TrainingTypeResponse;
import epam.task.gymbootdb.dto.UserCreateOrUpdateRequest;
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

        TrainerResponse response = mapper.toDto(trainer);

        assertTrainerResponse(trainer, response);
    }

    @Test
    public void testToEntity() {
        TrainerCreateOrUpdateRequest request = getRequest(getUserCreateOrUpdateRequest());

        Trainer trainer = mapper.toEntity(request);

        assertEquals(request.getTrainingType().getId(), trainer.getTrainingType().getId());
        assertEquals(request.getTrainingType().getName(), trainer.getTrainingType().getName());
        assertEquals(request.getUser().getFirstName(), trainer.getUser().getFirstName());
        assertEquals(request.getUser().getLastName(), trainer.getUser().getLastName());
    }

    @Test
    public void testToDtoList() {
        Trainer trainer1 = getTrainer1(getUser1());
        Trainer trainer2 = getTrainer2(getUser2());
        List<Trainer> trainers = List.of(trainer1, trainer2);

        List<TrainerResponse> trainerResponses = mapper.toDtoList(trainers);

        assertTrainerResponse(trainer1, trainerResponses.get(0));
        assertTrainerResponse(trainer2, trainerResponses.get(1));
    }

    private static void assertTrainerResponse(Trainer trainer, TrainerResponse response) {
        assertEquals(trainer.getId(), response.getId());
        assertEquals(trainer.getTrainingType().getId(), response.getTrainingType().getId());
        assertEquals(trainer.getTrainingType().getName(), response.getTrainingType().getName());
        assertEquals(trainer.getUser().getId(), response.getUser().getId());
        assertEquals(trainer.getUser().getFirstName(), response.getUser().getFirstName());
        assertEquals(trainer.getUser().getLastName(), response.getUser().getLastName());
        assertEquals(trainer.getUser().getUsername(), response.getUser().getUsername());
        assertEquals(trainer.getUser().isActive(), response.getUser().isActive());
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

    private static Trainer getTrainer1(User user) {
        return Trainer.builder()
                .id(1L)
                .user(user)
                .trainingType(new TrainingType(1, "YOGA")).build();
    }

    private static Trainer getTrainer2(User user) {
        return Trainer.builder()
                .id(2L)
                .user(user)
                .trainingType(new TrainingType(2, "BOXING")).build();

    }

    private static TrainerCreateOrUpdateRequest getRequest(UserCreateOrUpdateRequest user) {
        return TrainerCreateOrUpdateRequest.builder()
                .id(1L)
                .user(user)
                .trainingType(new TrainingTypeResponse(1, "YOGA")).build();

    }

    private static UserCreateOrUpdateRequest getUserCreateOrUpdateRequest() {
        return UserCreateOrUpdateRequest.builder().firstName("John").lastName("Doe").build();
    }
}
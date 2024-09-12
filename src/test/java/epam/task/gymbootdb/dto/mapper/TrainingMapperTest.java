package epam.task.gymbootdb.dto.mapper;

import epam.task.gymbootdb.dto.TrainingCreateRequest;
import epam.task.gymbootdb.dto.TrainingResponse;
import epam.task.gymbootdb.entity.Training;
import epam.task.gymbootdb.entity.Trainer;
import epam.task.gymbootdb.entity.Trainee;
import epam.task.gymbootdb.entity.TrainingType;
import epam.task.gymbootdb.entity.User;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;

public class TrainingMapperTest {

    private final TrainingMapper mapper = Mappers.getMapper(TrainingMapper.class);

    @Test
    public void testToDto() {
        Training training = getTraining1(getTrainer1(getUser1()), getTrainee1(getUser2()), getTrainingType1());

        TrainingResponse response = mapper.toDto(training);

        assertTrainingResponse(training, response);
    }

    @Test
    public void testToEntity() {
        TrainingCreateRequest request = getTrainingCreateRequest();

        Training training = mapper.toEntity(request);

        assertEquals(request.getName(), training.getName());
        assertEquals(request.getDate(), training.getDate());
        assertEquals(request.getDuration(), training.getDuration());
        assertEquals(request.getTrainerId(), training.getTrainer().getId());
        assertEquals(request.getTraineeId(), training.getTrainee().getId());
        assertEquals(request.getTrainingTypeId(), training.getTrainingType().getId());
    }

    @Test
    public void testToDtoList() {
        Training training1 = getTraining1(getTrainer1(getUser1()), getTrainee1(getUser2()), getTrainingType1());
        Training training2 = getTraining2(getTrainer2(getUser3()), getTrainee2(getUser4()), getTrainingType2());

        List<Training> trainings = List.of(training1, training2);

        List<TrainingResponse> trainingResponses = mapper.toDtoList(trainings);

        assertTrainingResponse(training1, trainingResponses.get(0));
        assertTrainingResponse(training2, trainingResponses.get(1));
    }

    private static void assertTrainingResponse(Training training, TrainingResponse response) {
        assertEquals(training.getId(), response.getId());
        assertEquals(training.getName(), response.getName());
        assertEquals(training.getDate(), response.getDate());
        assertEquals(training.getDuration(), response.getDuration());
        assertEquals(training.getTrainer().getId(), response.getTrainer().getId());
        assertEquals(training.getTrainee().getId(), response.getTrainee().getId());
        assertEquals(training.getTrainingType().getId(), response.getTrainingType().getId());
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

    private static User getUser3() {
        return User.builder()
                .id(3L)
                .firstName("Alice")
                .lastName("Smith")
                .username("Alice.Smith")
                .isActive(true)
                .build();
    }

    private static User getUser4() {
        return User.builder()
                .id(4L)
                .firstName("Bob")
                .lastName("Johnson")
                .username("Bob.Johnson")
                .isActive(true)
                .build();
    }

    private static Trainer getTrainer1(User user) {
        return Trainer.builder()
                .id(1L)
                .user(user)
                .trainingType(new TrainingType(1, "Yoga"))
                .build();
    }

    private static Trainer getTrainer2(User user) {
        return Trainer.builder()
                .id(2L)
                .user(user)
                .trainingType(new TrainingType(2, "Boxing"))
                .build();
    }

    private static Trainee getTrainee1(User user) {
        return Trainee.builder()
                .id(1L)
                .user(user)
                .build();
    }

    private static Trainee getTrainee2(User user) {
        return Trainee.builder()
                .id(2L)
                .user(user)
                .build();
    }

    private static TrainingType getTrainingType1() {
        return TrainingType.builder()
                .id(1L)
                .name("YOGA")
                .build();
    }

    private static TrainingType getTrainingType2() {
        return TrainingType.builder()
                .id(2L)
                .name("BOXING")
                .build();
    }

    private static Training getTraining1(Trainer trainer, Trainee trainee, TrainingType trainingType) {
        return Training.builder()
                .id(1L)
                .name("Morning Yoga")
                .date(LocalDate.of(2024, 12, 12))
                .duration(60)
                .trainer(trainer)
                .trainee(trainee)
                .trainingType(trainingType)
                .build();
    }

    private static Training getTraining2(Trainer trainer, Trainee trainee, TrainingType trainingType) {
        return Training.builder()
                .id(2L)
                .name("Evening Boxing")
                .date(LocalDate.of(2024, 12, 13))
                .duration(45)
                .trainer(trainer)
                .trainee(trainee)
                .trainingType(trainingType)
                .build();
    }

    private static TrainingCreateRequest getTrainingCreateRequest() {
        return TrainingCreateRequest.builder()
                .name("Morning Yoga")
                .date(LocalDate.of(2024, 12, 12))
                .duration(60)
                .trainerId(1L)
                .traineeId(2L)
                .trainingTypeId(1L)
                .build();
    }
}
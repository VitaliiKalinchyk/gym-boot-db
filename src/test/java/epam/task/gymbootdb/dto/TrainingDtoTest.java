package epam.task.gymbootdb.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrainingDtoTest {

    public static final String NAME = "Yoga Class";
    public static final LocalDate DATE = LocalDate.now().plusDays(1);
    public static final int DURATION = 60;
    public static final long ID = 1L;

    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validTrainingCreateRequest() {
        assertNoViolations(createRequest(NAME, DATE, DURATION, ID, ID, ID));
    }

    @Test
    void nameIsNull() {
        TrainingDto request = createRequest(null, DATE, DURATION, ID, ID, ID);

        assertSingleViolation(request, "Name cannot be null");
    }

    @Test
    void nameTooShort() {
        TrainingDto request = createRequest("A", DATE, DURATION, ID, ID, ID);

        assertSingleViolation(request, "Name must be between 3 and 45 characters and " +
                "contain only letters, digits, dots or white spaces");
    }

    @Test
    void nameTooLong() {
        TrainingDto request = createRequest("A".repeat(46), DATE, DURATION, ID, ID, ID);

        assertSingleViolation(request, "Name must be between 3 and 45 characters and " +
                "contain only letters, digits, dots or white spaces");
    }

    @Test
    void nameWithSpecialCharacters() {
        TrainingDto request = createRequest(NAME +"_", DATE, DURATION, ID, ID, ID);

        assertSingleViolation(request, "Name must be between 3 and 45 characters and " +
                "contain only letters, digits, dots or white spaces");
    }

    @Test
    void nullDate() {
        TrainingDto request = createRequest(NAME, null, DURATION, ID, ID, ID);

        assertSingleViolation(request, "Date is required");
    }

    @Test
    void dateInPast() {
        TrainingDto request = createRequest(NAME, LocalDate.now().minusDays(1), DURATION, ID, ID, ID);

        assertSingleViolation(request, "Date must be in the future");
    }

    @Test
    void durationTooShort() {
        TrainingDto request = createRequest(NAME, DATE,9, ID, ID, ID);

        assertSingleViolation(request, "Duration must be greater than 10");
    }

    @Test
    void durationTooLong() {
        TrainingDto request = createRequest(NAME, DATE,301, ID, ID, ID);

        assertSingleViolation(request, "Duration mustn't be greater than 300");
    }

    @Test
    void noTrainer() {
        TrainingDto request = createRequest(NAME, DATE,DURATION, 0, ID, ID);

        assertSingleViolation(request, "Trainer is required");
    }

    @Test
    void noTrainee() {
        TrainingDto request = createRequest(NAME, DATE,DURATION, ID, 0, ID);

        assertSingleViolation(request, "Trainee is required");
    }

    @Test
    void noTrainingType() {
        TrainingDto request = createRequest(NAME, DATE,DURATION, ID, ID, 0);

        assertSingleViolation(request, "TrainingType is required");
    }

    private TrainingDto createRequest(String name, LocalDate date, int duration,
                                             long trainerId, long traineeId, long trainingTypeId) {
        return TrainingDto.builder()
                .name(name)
                .date(date)
                .duration(duration)
                .trainer(trainerId == 0 ? null : TrainerDto.builder().id(trainerId).build())
                .trainee(traineeId == 0 ? null : TraineeDto.builder().id(traineeId).build())
                .trainingType(trainingTypeId == 0 ? null : TrainingTypeDto.builder().id(trainingTypeId).build())
                .build();
    }

    private void assertNoViolations(TrainingDto request) {
        Set<ConstraintViolation<TrainingDto>> violations = validator.validate(request);
        assertEquals(0, violations.size(), "Validation should pass for valid data");
    }

    private void assertSingleViolation(TrainingDto request, String expectedMessage) {
        Set<ConstraintViolation<TrainingDto>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        ConstraintViolation<TrainingDto> violation = violations.iterator().next();
        assertEquals(expectedMessage, violation.getMessage());
    }
}

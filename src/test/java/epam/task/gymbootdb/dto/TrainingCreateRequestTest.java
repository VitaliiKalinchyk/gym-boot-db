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

public class TrainingCreateRequestTest {

    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void testValidTrainingCreateRequest() {
        Set<ConstraintViolation<TrainingCreateRequest>> violations = validator.validate(validRequest());

        assertEquals(0, violations.size(), "Validation should pass for valid data");
    }

    @Test
    public void testBlankName() {
        TrainingCreateRequest request = validRequest();
        request.setName("");

        Set<ConstraintViolation<TrainingCreateRequest>> violations = validator.validate(request);
        assertEquals(2, violations.size(), "Validation should fail for blank name");
    }

    @Test
    public void testNameTooShort() {
        TrainingCreateRequest request = validRequest();
        request.setName("A");

        Set<ConstraintViolation<TrainingCreateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for name shorter than 2 characters");

        ConstraintViolation<TrainingCreateRequest> violation = violations.iterator().next();
        assertEquals("Name must be between 2 and 45 characters", violation.getMessage());
    }

    @Test
    public void testNameTooLong() {
        TrainingCreateRequest request = validRequest();
        request.setName("A".repeat(46));

        Set<ConstraintViolation<TrainingCreateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for name longer than 45 characters");

        ConstraintViolation<TrainingCreateRequest> violation = violations.iterator().next();
        assertEquals("Name must be between 2 and 45 characters", violation.getMessage());
    }

    @Test
    public void testDateInPast() {
        TrainingCreateRequest request = validRequest();
        request.setDate(LocalDate.now().minusDays(1));

        Set<ConstraintViolation<TrainingCreateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for date in the past");

        ConstraintViolation<TrainingCreateRequest> violation = violations.iterator().next();
        assertEquals("Date must be in the future", violation.getMessage());
    }

    @Test
    public void testNullDate() {
        TrainingCreateRequest request = validRequest();
        request.setDate(null);

        Set<ConstraintViolation<TrainingCreateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for null date");

        ConstraintViolation<TrainingCreateRequest> violation = violations.iterator().next();
        assertEquals("Date is required", violation.getMessage());
    }

    @Test
    public void testInvalidDuration() {
        TrainingCreateRequest request = validRequest();
        request.setDuration(0);

        Set<ConstraintViolation<TrainingCreateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for duration less than 1");

        ConstraintViolation<TrainingCreateRequest> violation = violations.iterator().next();
        assertEquals("Duration must be greater than 0", violation.getMessage());
    }

    @Test
    public void testDurationTooLong() {
        TrainingCreateRequest request = validRequest();
        request.setDuration(301);

        Set<ConstraintViolation<TrainingCreateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for duration greater than 300");

        ConstraintViolation<TrainingCreateRequest> violation = violations.iterator().next();
        assertEquals("Duration mustn't be greater than 300", violation.getMessage());
    }

    @Test
    public void testNegativeTrainerId() {
        TrainingCreateRequest request = validRequest();
        request.setTrainerId(-1);

        Set<ConstraintViolation<TrainingCreateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for negative trainer ID");

        ConstraintViolation<TrainingCreateRequest> violation = violations.iterator().next();
        assertEquals("Trainer ID must be a positive number", violation.getMessage());
    }

    @Test
    public void testZeroTraineeId() {
        TrainingCreateRequest request = validRequest();
        request.setTraineeId(0);

        Set<ConstraintViolation<TrainingCreateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for zero trainee ID");

        ConstraintViolation<TrainingCreateRequest> violation = violations.iterator().next();
        assertEquals("Trainee ID must be a positive number", violation.getMessage());
    }

    @Test
    public void testNegativeTrainingTypeId() {
        TrainingCreateRequest request = validRequest();
        request.setTrainingTypeId(-1);

        Set<ConstraintViolation<TrainingCreateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for negative training type ID");

        ConstraintViolation<TrainingCreateRequest> violation = violations.iterator().next();
        assertEquals("Training Type ID must be a positive number", violation.getMessage());
    }

    private static TrainingCreateRequest validRequest() {
        return TrainingCreateRequest.builder()
                .name("Yoga Class")
                .date(LocalDate.now().plusDays(1))
                .duration(60)
                .trainerId(1)
                .traineeId(2)
                .trainingTypeId(3)
                .build();
    }
}

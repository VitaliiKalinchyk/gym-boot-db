package epam.task.gymbootdb.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrainerCreateOrUpdateRequestTest {

    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void testValidTrainerCreateOrUpdateRequest() {
        Set<ConstraintViolation<TrainerCreateOrUpdateRequest>> violations = validator.validate(validRequest());

        assertEquals(0, violations.size(), "Validation should pass for valid data");
    }

    @Test
    public void testNullUser() {
        TrainerCreateOrUpdateRequest request = validRequest();
        request.setUser(null);

        Set<ConstraintViolation<TrainerCreateOrUpdateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for null user");

        ConstraintViolation<TrainerCreateOrUpdateRequest> violation = violations.iterator().next();
        assertEquals("User cannot be null", violation.getMessage());
    }

    @Test
    public void testNullTrainingType() {
        TrainerCreateOrUpdateRequest request = validRequest();
        request.setTrainingType(null);

        Set<ConstraintViolation<TrainerCreateOrUpdateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for null training type");

        ConstraintViolation<TrainerCreateOrUpdateRequest> violation = violations.iterator().next();
        assertEquals("TrainingType cannot be null", violation.getMessage());
    }

    private static TrainerCreateOrUpdateRequest validRequest() {
        return TrainerCreateOrUpdateRequest.builder()
                .id(1L)
                .user(new UserCreateOrUpdateRequest("John", "Doe"))
                .trainingType(new TrainingTypeResponse(1L, "Fitness"))
                .build();
    }
}
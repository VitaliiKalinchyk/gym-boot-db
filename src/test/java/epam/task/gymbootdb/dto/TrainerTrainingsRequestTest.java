package epam.task.gymbootdb.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrainerTrainingsRequestTest {

    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void testValidTrainerTrainingsRequest() {
        Set<ConstraintViolation<TrainerTrainingsRequest>> violations = validator.validate(validRequest());

        assertEquals(0, violations.size(), "Validation should pass for valid data");
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testNullOrEmptyTrainerUsername(String trainerUsername) {
        TrainerTrainingsRequest request = validRequest();
        request.setTrainerUsername(trainerUsername);

        Set<ConstraintViolation<TrainerTrainingsRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for null or empty trainer username");

        ConstraintViolation<TrainerTrainingsRequest> violation = violations.iterator().next();
        assertEquals("Trainer username cannot be null or empty", violation.getMessage());
    }

    @Test
    public void testValidOptionalFields() {
        TrainerTrainingsRequest request = validRequest();
        request.setFromDate(LocalDate.now().minusDays(1));
        request.setToDate(LocalDate.now());
        request.setTraineeUsername("trainee123");

        Set<ConstraintViolation<TrainerTrainingsRequest>> violations = validator.validate(request);
        assertEquals(0, violations.size(), "Validation should pass for valid optional fields");
    }

    private static TrainerTrainingsRequest validRequest() {
        return TrainerTrainingsRequest.builder()
                .trainerUsername("trainer123")
                .build();
    }
}

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

public class TraineeTrainingsRequestTest {

    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void testValidTraineeTrainingsRequest() {
        Set<ConstraintViolation<TraineeTrainingsRequest>> violations = validator.validate(validRequest());

        assertEquals(0, violations.size(), "Validation should pass for valid data");
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testNullOrEmptyTraineeUsername(String traineeUsername) {
        TraineeTrainingsRequest request = validRequest();
        request.setTraineeUsername(traineeUsername);

        Set<ConstraintViolation<TraineeTrainingsRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for null or empty trainee username");

        ConstraintViolation<TraineeTrainingsRequest> violation = violations.iterator().next();
        assertEquals("Trainee username cannot be null or empty", violation.getMessage());
    }

    @Test
    public void testValidOptionalFields() {
        TraineeTrainingsRequest request = validRequest();
        request.setFromDate(LocalDate.now().minusDays(1));
        request.setToDate(LocalDate.now());
        request.setTraineeUsername("trainee123");
        request.setTrainingTypeName("YOGA");

        Set<ConstraintViolation<TraineeTrainingsRequest>> violations = validator.validate(request);
        assertEquals(0, violations.size(), "Validation should pass for valid optional fields");
    }

    private static TraineeTrainingsRequest validRequest() {
        return TraineeTrainingsRequest.builder()
                .traineeUsername("validUsername")
                .build();
    }
}

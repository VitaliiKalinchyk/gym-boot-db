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

public class TraineeCreateOrUpdateRequestTest {

    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void testValidTraineeCreateOrUpdateRequest() {
        Set<ConstraintViolation<TraineeCreateOrUpdateRequest>> violations = validator.validate(validRequest());

        assertEquals(0, violations.size(), "Validation should pass for valid data");
    }


    @Test
    public void testNullUser() {
        TraineeCreateOrUpdateRequest request = validRequest();
        request.setUser(null);

        Set<ConstraintViolation<TraineeCreateOrUpdateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for null user");

        ConstraintViolation<TraineeCreateOrUpdateRequest> violation = violations.iterator().next();
        assertEquals("User cannot be null", violation.getMessage());
    }

    @Test
    public void testFutureBirthday() {
        TraineeCreateOrUpdateRequest request = validRequest();
        request.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<TraineeCreateOrUpdateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for future birthday");
        ConstraintViolation<TraineeCreateOrUpdateRequest> violation = violations.iterator().next();
        assertEquals("Birthday cannot be in the future", violation.getMessage());
    }

    @Test
    public void testAddressTooLong() {
        TraineeCreateOrUpdateRequest request = validRequest();
        request.setAddress("A".repeat(121));

        Set<ConstraintViolation<TraineeCreateOrUpdateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for address exceeding 120 characters");
        ConstraintViolation<TraineeCreateOrUpdateRequest> violation = violations.iterator().next();
        assertEquals("Address cannot exceed 120 characters", violation.getMessage());
    }

    private static TraineeCreateOrUpdateRequest validRequest() {
        return TraineeCreateOrUpdateRequest.builder()
                .id(1)
                .user(UserDto.builder().firstName("Joe").lastName("Doe").build())
                .birthday(LocalDate.of(2000, 1, 1))
                .address("123 Main Street")
                .build();
    }
}
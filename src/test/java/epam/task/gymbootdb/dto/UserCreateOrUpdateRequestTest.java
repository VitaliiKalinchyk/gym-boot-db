package epam.task.gymbootdb.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserCreateOrUpdateRequestTest {

    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void testValidUserCreateOrUpdateRequest() {
        Set<ConstraintViolation<UserCreateOrUpdateRequest>> violations = validator.validate(validRequest());

        assertEquals(0, violations.size(), "Validation should pass for valid data");
    }

    @Test
    public void testNullFirstName() {
        UserCreateOrUpdateRequest request = validRequest();
        request.setFirstName(null);

        Set<ConstraintViolation<UserCreateOrUpdateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for null first name");

        ConstraintViolation<UserCreateOrUpdateRequest> violation = violations.iterator().next();
        assertEquals("First name cannot be null or empty", violation.getMessage());
    }

    @Test
    public void testNullLastName() {
        UserCreateOrUpdateRequest request = validRequest();
        request.setLastName(null);

        Set<ConstraintViolation<UserCreateOrUpdateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for null last name");

        ConstraintViolation<UserCreateOrUpdateRequest> violation = violations.iterator().next();
        assertEquals("Last name cannot be null or empty", violation.getMessage());
    }

    @Test
    public void testEmptyFirstName() {
        UserCreateOrUpdateRequest request = validRequest();
        request.setFirstName("");

        Set<ConstraintViolation<UserCreateOrUpdateRequest>> violations = validator.validate(request);
        assertEquals(3, violations.size(), "Validation should fail for empty first name");
    }

    @Test
    public void testEmptyLastName() {
        UserCreateOrUpdateRequest request = validRequest();
        request.setLastName("");

        Set<ConstraintViolation<UserCreateOrUpdateRequest>> violations = validator.validate(request);
        assertEquals(3, violations.size(), "Validation should fail for empty last name");
    }

    @Test
    public void testFirstNameTooLong() {
        UserCreateOrUpdateRequest request = validRequest();
        request.setFirstName("A".repeat(46));

        Set<ConstraintViolation<UserCreateOrUpdateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for first name exceeding 45 characters");

        ConstraintViolation<UserCreateOrUpdateRequest> violation = violations.iterator().next();
        assertEquals("First name must be between 1 and 45 characters", violation.getMessage());
    }

    @Test
    public void testLastNameTooLong() {
        UserCreateOrUpdateRequest request = validRequest();
        request.setLastName("A".repeat(46));

        Set<ConstraintViolation<UserCreateOrUpdateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for last name exceeding 45 characters");

        ConstraintViolation<UserCreateOrUpdateRequest> violation = violations.iterator().next();
        assertEquals("Last name must be between 1 and 45 characters", violation.getMessage());
    }

    @Test
    public void testFirstNameWithDigits() {
        UserCreateOrUpdateRequest request = validRequest();
        request.setFirstName("Joe1");

        Set<ConstraintViolation<UserCreateOrUpdateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for first name with digits");

        ConstraintViolation<UserCreateOrUpdateRequest> violation = violations.iterator().next();
        assertEquals("First name must contain only letters", violation.getMessage());
    }

    @Test
    public void testLastNameWithDigits() {
        UserCreateOrUpdateRequest request = validRequest();
        request.setLastName("Doe1");

        Set<ConstraintViolation<UserCreateOrUpdateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for last name with digits");

        ConstraintViolation<UserCreateOrUpdateRequest> violation = violations.iterator().next();
        assertEquals("Last name must contain only letters", violation.getMessage());
    }

    @Test
    public void testFirstNameWithSpecialCharacters() {
        UserCreateOrUpdateRequest request = validRequest();
        request.setFirstName("John@Doe");

        Set<ConstraintViolation<UserCreateOrUpdateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for first name with special characters");

        ConstraintViolation<UserCreateOrUpdateRequest> violation = violations.iterator().next();
        assertEquals("First name must contain only letters", violation.getMessage());
    }

    @Test
    public void testLastNameWithSpecialCharacters() {
        UserCreateOrUpdateRequest request = validRequest();
        request.setLastName("Doe@");

        Set<ConstraintViolation<UserCreateOrUpdateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for last name with special characters");

        ConstraintViolation<UserCreateOrUpdateRequest> violation = violations.iterator().next();
        assertEquals("Last name must contain only letters", violation.getMessage());
    }

    private static UserCreateOrUpdateRequest validRequest() {
        return new UserCreateOrUpdateRequest("John", "Doe");
    }
}

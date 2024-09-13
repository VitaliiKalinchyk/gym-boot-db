package epam.task.gymbootdb.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserCredentialsTest {

    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void testValidUserCredentials() {
        Set<ConstraintViolation<UserCredentials>> violations = validator.validate(validRequest());

        assertEquals(0, violations.size(), "Validation should pass for valid data");
    }

    @Test
    public void testNullUsername() {
        UserCredentials credentials = validRequest();
        credentials.setUsername(null);

        Set<ConstraintViolation<UserCredentials>> violations = validator.validate(credentials);
        assertEquals(1, violations.size(), "Validation should fail for null username");

        ConstraintViolation<UserCredentials> violation = violations.iterator().next();
        assertEquals("Username cannot be null or empty", violation.getMessage());
    }

    @Test
    public void testNullPassword() {
        UserCredentials credentials = validRequest();
        credentials.setPassword(null);

        Set<ConstraintViolation<UserCredentials>> violations = validator.validate(credentials);
        assertEquals(1, violations.size(), "Validation should fail for null password");

        ConstraintViolation<UserCredentials> violation = violations.iterator().next();
        assertEquals("Password cannot be null or empty", violation.getMessage());
    }

    @Test
    public void testEmptyUsername() {
        UserCredentials credentials = validRequest();
        credentials.setUsername("");

        Set<ConstraintViolation<UserCredentials>> violations = validator.validate(credentials);
        assertEquals(2, violations.size(), "Validation should fail for empty username");
    }

    @Test
    public void testEmptyPassword() {
        UserCredentials credentials = validRequest();
        credentials.setPassword("");

        Set<ConstraintViolation<UserCredentials>> violations = validator.validate(credentials);
        assertEquals(2, violations.size(), "Validation should fail for empty password");
    }

    @Test
    public void testUsernameLength() {
        UserCredentials credentials = validRequest();
        credentials.setUsername("s");

        Set<ConstraintViolation<UserCredentials>> violations = validator.validate(credentials);
        assertEquals(1, violations.size(),
                "Validation should fail for username not in between 3 and 100 characters");

        ConstraintViolation<UserCredentials> violation = violations.iterator().next();
        assertEquals("Username must be between 3 and 100 characters", violation.getMessage());
    }

    @Test
    public void testPasswordLength() {
        UserCredentials credentials = validRequest();
        credentials.setPassword("short");

        Set<ConstraintViolation<UserCredentials>> violations = validator.validate(credentials);
        assertEquals(1, violations.size(),
                "Validation should fail for password not in between 8 16 100 characters");

        ConstraintViolation<UserCredentials> violation = violations.iterator().next();
        assertEquals("Password must be between 8 and 16 characters", violation.getMessage());
    }

    private static UserCredentials validRequest() {
        return new UserCredentials("username", "password12");
    }
}

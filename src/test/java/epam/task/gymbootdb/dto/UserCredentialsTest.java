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

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void testValidUserCredentials() {
        assertNoViolations(createCredentials(USERNAME, PASSWORD));
    }

    @Test
    public void testNullUsername() {
        UserCredentials credentials = createCredentials(null, PASSWORD);

        assertSingleViolation(credentials, "Username cannot be null");
    }

    @Test
    public void testNullPassword() {
        UserCredentials credentials = createCredentials(USERNAME, null);

        assertSingleViolation(credentials, "Password cannot be null");
    }

    @Test
    public void testUsernameLength() {
        UserCredentials credentials = createCredentials("s", PASSWORD);

        assertSingleViolation(credentials, "Username must be between 3 and 100 characters");
    }

    @Test
    public void testPasswordLength() {
        UserCredentials credentials = createCredentials(USERNAME, "short");

        assertSingleViolation(credentials, "Password must be between 8 and 16 characters");
    }

    private static UserCredentials createCredentials(String username, String password) {
        return new UserCredentials(username, password);
    }

    private void assertNoViolations(UserCredentials credentials) {
        Set<ConstraintViolation<UserCredentials>> violations = validator.validate(credentials);
        assertEquals(0, violations.size(), "Validation should pass for valid data");
    }

    private void assertSingleViolation(UserCredentials credentials, String expectedMessage) {
        Set<ConstraintViolation<UserCredentials>> violations = validator.validate(credentials);
        assertEquals(1, violations.size());
        ConstraintViolation<UserCredentials> violation = violations.iterator().next();
        assertEquals(expectedMessage, violation.getMessage());
    }
}
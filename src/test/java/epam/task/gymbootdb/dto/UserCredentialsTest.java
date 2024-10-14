package epam.task.gymbootdb.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserCredentialsTest {

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
    void validUserCredentials() {
        assertNoViolations(createCredentials(USERNAME, PASSWORD));
    }

    @Test
    void nullUsername() {
        UserCredentials credentials = createCredentials(null, PASSWORD);

        assertSingleViolation(credentials, "Username is required");
    }

    @Test
    void nullPassword() {
        UserCredentials credentials = createCredentials(USERNAME, null);

        assertSingleViolation(credentials, "Password is required");
    }

    private UserCredentials createCredentials(String username, String password) {
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

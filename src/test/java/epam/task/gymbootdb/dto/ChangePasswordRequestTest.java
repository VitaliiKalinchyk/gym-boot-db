package epam.task.gymbootdb.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ChangePasswordRequestTest {

    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void testValidChangePasswordRequest() {
        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(validRequest());

        assertEquals(0, violations.size(), "Validation should pass for valid data");
    }

    @Test
    public void testNullUserCredentials() {
        ChangePasswordRequest request = validRequest();
        request.setUserCredentials(null);

        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for null user credentials");

        ConstraintViolation<ChangePasswordRequest> violation = violations.iterator().next();
        assertEquals("User credentials cannot be null", violation.getMessage());
    }

    @Test
    public void testUserCredentialsUsernameIsNotValid() {
        ChangePasswordRequest request = validRequest();
        request.getUserCredentials().setUsername(null);

        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for null user credentials");

        ConstraintViolation<ChangePasswordRequest> violation = violations.iterator().next();
        assertEquals("Username cannot be null or empty", violation.getMessage());
    }

    @Test
    public void testUserCredentialsPasswordIsNotValid() {
        ChangePasswordRequest request = validRequest();
        request.getUserCredentials().setPassword(null);

        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for null user credentials");

        ConstraintViolation<ChangePasswordRequest> violation = violations.iterator().next();
        assertEquals("Password cannot be null or empty", violation.getMessage());
    }

    @Test
    public void testNullNewPassword() {
        ChangePasswordRequest request = validRequest();
        request.setNewPassword(null);

        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Validation should fail for null new password");

        ConstraintViolation<ChangePasswordRequest> violation = violations.iterator().next();
        assertEquals("New password cannot be null or empty", violation.getMessage());
    }

    @Test
    public void testEmptyNewPassword() {
        ChangePasswordRequest request = validRequest();
        request.setNewPassword("");

        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);
        assertEquals(2, violations.size(), "Validation should fail for empty new password");
    }

    @Test
    public void testNewPasswordTooShort() {
        ChangePasswordRequest request = validRequest();
        request.setNewPassword("short");

        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(),
                "Validation should fail for password not in between 8 and 16 characters");

        ConstraintViolation<ChangePasswordRequest> violation = violations.iterator().next();
        assertEquals("New password must be between 8 and 16 characters", violation.getMessage());
    }

    @Test
    public void testNewPasswordTooLong() {
        ChangePasswordRequest request = validRequest();
        request.setNewPassword("veryLongPassword123");

        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(),
                "Validation should fail for password longer than 16 characters");

        ConstraintViolation<ChangePasswordRequest> violation = violations.iterator().next();
        assertEquals("New password must be between 8 and 16 characters", violation.getMessage());
    }

    private static ChangePasswordRequest validRequest() {
        return ChangePasswordRequest.builder()
                .userCredentials(new UserCredentials("validUsername", "validPassword"))
                .newPassword("validNewPass12")
                .build();
    }
}

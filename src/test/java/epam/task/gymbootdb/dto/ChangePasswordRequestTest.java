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

    private static final String OLD_PASSWORD = "oldPassword";
    private static final String NEW_PASSWORD = "newPassword";

    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void testValidChangePasswordRequest() {
        assertNoViolations(createRequest(OLD_PASSWORD, NEW_PASSWORD));
    }

    @Test
    public void testUserCredentialsPasswordIsNotValid() {
        ChangePasswordRequest request = createRequest(null, NEW_PASSWORD);

        assertSingleViolation(request, "Old password cannot be null");
    }

    @Test
    public void testNullNewPassword() {
        ChangePasswordRequest request = createRequest(OLD_PASSWORD, null);

        assertSingleViolation(request, "New password cannot be null");
    }

    @Test
    public void testNewPasswordTooShort() {
        ChangePasswordRequest request = createRequest(OLD_PASSWORD, "short");

        assertSingleViolation(request, "New password must be between 8 and 16 characters");
    }

    @Test
    public void testNewPasswordTooLong() {
        ChangePasswordRequest request = createRequest(OLD_PASSWORD, "veryLongPassword123");

        assertSingleViolation(request, "New password must be between 8 and 16 characters");
    }

    private static ChangePasswordRequest createRequest(String oldPassword, String newPassword) {
        return ChangePasswordRequest.builder()
                .id(1L)
                .oldPassword(oldPassword)
                .newPassword(newPassword)
                .build();
    }

    private void assertNoViolations(ChangePasswordRequest request) {
        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);
        assertEquals(0, violations.size(), "Validation should pass for valid data");
    }

    private void assertSingleViolation(ChangePasswordRequest request, String expectedMessage) {
        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        ConstraintViolation<ChangePasswordRequest> violation = violations.iterator().next();
        assertEquals(expectedMessage, violation.getMessage());
    }
}

package epam.task.gymbootdb.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ChangePasswordRequestTest {

    private static final String NEW_PASSWORD = "newPassword1";

    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validChangePasswordRequest() {

        assertNoViolations(createRequest(getValidUserCredentials(), NEW_PASSWORD));
    }

    @Test
    void nullUserCredentials() {
        ChangePasswordRequest request = createRequest(null, NEW_PASSWORD);

        assertSingleViolation(request, "UserCredentials is required");
    }

    @Test
    void invalidUserCredentials() {
        ChangePasswordRequest request = createRequest(getInvalidUserCredentials(), NEW_PASSWORD);

        assertSingleViolation(request, "Username is required");
    }

    @Test
    void nullNewPassword() {
        ChangePasswordRequest request = createRequest(getValidUserCredentials(), null);

        assertSingleViolation(request, "New password is required");
    }

    @Test
    void newPasswordTooShort() {
        ChangePasswordRequest request = createRequest(getValidUserCredentials(), "short");

        assertSingleViolation(request,
                "New password must be between 8 and 16 characters and contain only letters or digits");
    }


    @Test
    void newPasswordTooLong() {
        ChangePasswordRequest request = createRequest(getValidUserCredentials(), "veryLongPassword123");

        assertSingleViolation(request,
                "New password must be between 8 and 16 characters and contain only letters or digits");
    }

    @Test
    void newPasswordWithSpecialSymbol(){
        ChangePasswordRequest request = createRequest(getValidUserCredentials(), NEW_PASSWORD + "_");

        assertSingleViolation(request,
                "New password must be between 8 and 16 characters and contain only letters or digits");
    }

    private ChangePasswordRequest createRequest(UserCredentials userCredentials, String newPassword) {
        return new ChangePasswordRequest(userCredentials, newPassword);
    }

    private UserCredentials getValidUserCredentials() {
        return new UserCredentials("Joe", "oldPassword");
    }

    private UserCredentials getInvalidUserCredentials() {
        return new UserCredentials(null, "oldPassword");
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

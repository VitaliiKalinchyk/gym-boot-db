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

public class TrainingCreateRequestTest {

    public static final String NAME = "Yoga Class";
    public static final LocalDate DATE = LocalDate.now().plusDays(1);
    public static final int DURATION = 60;

    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void testValidTrainingCreateRequest() {
        assertNoViolations(createRequest(NAME, DATE, DURATION));
    }

    @Test
    public void testNameIsNull() {
        TrainingCreateRequest request = createRequest(null, DATE, DURATION);

        assertSingleViolation(request, "Name cannot be null");
    }

    @Test
    public void testNameTooShort() {
        TrainingCreateRequest request = createRequest("A", DATE, DURATION);

        assertSingleViolation(request, "Name must be between 2 and 45 characters");
    }

    @Test
    public void testNameTooLong() {
        TrainingCreateRequest request = createRequest("A".repeat(46), DATE, DURATION);

        assertSingleViolation(request, "Name must be between 2 and 45 characters");

    }

    @Test
    public void testNullDate() {
        TrainingCreateRequest request = createRequest(NAME, null, DURATION);

        assertSingleViolation(request, "Date is required");
    }

    @Test
    public void testDateInPast() {
        TrainingCreateRequest request = createRequest(NAME, LocalDate.now().minusDays(1), DURATION);

        assertSingleViolation(request, "Date must be in the future");
    }

    @Test
    public void testDurationTooShort() {
        TrainingCreateRequest request = createRequest(NAME, DATE,9);

        assertSingleViolation(request, "Duration must be greater than 10");
    }

    @Test
    public void testDurationTooLong() {
        TrainingCreateRequest request = createRequest(NAME, DATE,301);

        assertSingleViolation(request, "Duration mustn't be greater than 300");
    }

    private static TrainingCreateRequest createRequest(String name, LocalDate date, int duration) {
        return new TrainingCreateRequest(name, date, duration, 1L, 2L, 3L);
    }

    private void assertNoViolations(TrainingCreateRequest request) {
        Set<ConstraintViolation<TrainingCreateRequest>> violations = validator.validate(request);
        assertEquals(0, violations.size(), "Validation should pass for valid data");
    }

    private void assertSingleViolation(TrainingCreateRequest request, String expectedMessage) {
        Set<ConstraintViolation<TrainingCreateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        ConstraintViolation<TrainingCreateRequest> violation = violations.iterator().next();
        assertEquals(expectedMessage, violation.getMessage());
    }
}
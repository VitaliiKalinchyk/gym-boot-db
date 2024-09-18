package epam.task.gymbootdb.dto;

import epam.task.gymbootdb.dto.validation.group.OnCreate;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TraineeDtoTest {

    public static final String FIRST_NAME = "Joe";
    public static final LocalDate BIRTHDAY = LocalDate.of(2000, 1, 1);
    public static final String ADDRESS = "123 Main Street";

    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void testValidTraineeDto() {
        assertNoViolations(createDto(FIRST_NAME, BIRTHDAY, ADDRESS));
    }

    @Test
    public void testValidTraineeDtoNullOptionalFields() {
        assertNoViolations(createDto(FIRST_NAME, null, null));
    }

    @Test
    public void testNullUser() {
        TraineeDto dto = createDto(FIRST_NAME, BIRTHDAY, ADDRESS);
        dto.setUser(null);

        assertSingleViolation(dto, "User cannot be null");
    }

    @Test
    public void testNotValidUser() {
        TraineeDto dto = createDto(null, BIRTHDAY, ADDRESS);

        assertSingleViolation(dto, "First name cannot be null");
    }

    @Test
    public void testFutureBirthday() {
        TraineeDto dto = createDto(FIRST_NAME, LocalDate.now().plusDays(1), ADDRESS);

        assertSingleViolation(dto, "Birthday cannot be in the future");
    }

    @Test
    public void testAddressTooLong() {
        TraineeDto dto = createDto(FIRST_NAME, BIRTHDAY, "A".repeat(121));

        assertSingleViolation(dto, "Address cannot exceed 120 characters");
    }

    private static TraineeDto createDto(String firstName, LocalDate date, String address) {
        UserDto user = UserDto.builder().firstName(firstName).lastName("Doe").build();
        return TraineeDto.builder()
                .user(user)
                .birthday(date)
                .address(address)
                .build();
    }

    private void assertSingleViolation(TraineeDto dto, String expectedMessage) {
        Set<ConstraintViolation<TraineeDto>> violations = validator.validate(dto, OnCreate.class);
        assertEquals(1, violations.size());
        ConstraintViolation<TraineeDto> violation = violations.iterator().next();
        assertEquals(expectedMessage, violation.getMessage());
    }

    private void assertNoViolations(TraineeDto dto) {
        Set<ConstraintViolation<TraineeDto>> violations = validator.validate(dto, OnCreate.class);
        assertEquals(0, violations.size(), "Validation should pass for valid data");
    }
}
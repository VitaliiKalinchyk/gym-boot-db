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

public class TraineeDtoTest {

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
        assertNoViolations(createDto(getUser(), BIRTHDAY, ADDRESS));
    }

    @Test
    public void testValidTraineeDtoNullOptionalFields() {
        assertNoViolations(createDto(getUser(), null, null));
    }

    @Test
    public void testNullUser() {
        TraineeDto dto = createDto(null, BIRTHDAY, ADDRESS);

        assertSingleViolation(dto, "User cannot be null");
    }

    @Test
    public void testNotValidUser() {
        TraineeDto dto = createDto(getUserNoFirstName(), BIRTHDAY, ADDRESS);

        assertSingleViolation(dto, "First name cannot be null");
    }

    @Test
    public void testFutureBirthday() {
        TraineeDto dto = createDto(getUser(), LocalDate.now().plusDays(1), ADDRESS);

        assertSingleViolation(dto, "Birthday cannot be in the future");
    }

    @Test
    public void testAddressTooLong() {
        TraineeDto dto = createDto(getUser(), BIRTHDAY, "A".repeat(121));

        assertSingleViolation(dto, "Address cannot exceed 120 characters");
    }

    private static TraineeDto createDto(UserDto user, LocalDate date, String address) {
        return TraineeDto.builder()
                .user(user)
                .birthday(date)
                .address(address)
                .build();
    }

    private static UserDto getUser() {
        return UserDto.builder().firstName("Joe").lastName("Doe").build();
    }

    private static UserDto getUserNoFirstName() {
        return UserDto.builder().lastName("Doe").build();
    }

    private void assertSingleViolation(TraineeDto dto, String expectedMessage) {
        Set<ConstraintViolation<TraineeDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        ConstraintViolation<TraineeDto> violation = violations.iterator().next();
        assertEquals(expectedMessage, violation.getMessage());
    }

    private void assertNoViolations(TraineeDto dto) {
        Set<ConstraintViolation<TraineeDto>> violations = validator.validate(dto);
        assertEquals(0, violations.size(), "Validation should pass for valid data");
    }
}
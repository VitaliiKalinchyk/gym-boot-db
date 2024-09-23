package epam.task.gymbootdb.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserDtoTest {

    private static final String FIRST_NAME = "Joe";
    private static final String LAST_NAME = "Doe";
    private static final String FIRST_NAME_ERROR =
            "First name must be between 1 and 45 characters and contain only letters";
    private static final String LAST_NAME_ERROR =
            "Last name must be between 1 and 45 characters and contain only letters";
    private static final String NULL_FIRST_NAME_ERROR = "First name cannot be null";
    private static final String NULL_LAST_NAME_ERROR = "Last name cannot be null";

    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testValidUserDto() {
        UserDto userDto = createUserDto(FIRST_NAME, LAST_NAME);

        assertNoViolations(userDto);
    }

    @Test
    void testNullFirstName() {
        UserDto userDto = createUserDto(null, LAST_NAME);

        assertSingleViolation(userDto, NULL_FIRST_NAME_ERROR);
    }

    @Test
    void testNullLastName() {
        UserDto userDto = createUserDto(FIRST_NAME, null);

        assertSingleViolation(userDto, NULL_LAST_NAME_ERROR);
    }

    @Test
    void testFirstNameTooLong() {
        UserDto userDto = createUserDto("A".repeat(46), LAST_NAME);

        assertSingleViolation(userDto, FIRST_NAME_ERROR);
    }

    @Test
    void testLastNameTooLong() {
        UserDto userDto = createUserDto(FIRST_NAME, "A".repeat(46));

        assertSingleViolation(userDto, LAST_NAME_ERROR);
    }

    @Test
    void testFirstNameWithDigits() {
        UserDto userDto = createUserDto("Joe1", LAST_NAME);

        assertSingleViolation(userDto, FIRST_NAME_ERROR);
    }

    @Test
    void testLastNameWithDigits() {
        UserDto userDto = createUserDto(FIRST_NAME, "Doe1");

        assertSingleViolation(userDto, LAST_NAME_ERROR);
    }

    private static UserDto createUserDto(String firstName, String lastName) {
        return UserDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }

    private void assertSingleViolation(UserDto dto, String expectedMessage) {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        ConstraintViolation<UserDto> violation = violations.iterator().next();
        assertEquals(expectedMessage, violation.getMessage());
    }

    private void assertNoViolations(UserDto dto) {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertEquals(0, violations.size(), "Validation should pass for valid data");
    }
}

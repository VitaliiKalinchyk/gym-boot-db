package epam.task.gymbootdb.dto;

import epam.task.gymbootdb.dto.validation.group.OnCreate;
import epam.task.gymbootdb.dto.validation.group.OnUpdate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserDtoTest {

    private static final String FIRST_NAME_ERROR = "First name must be between 1 and 45 characters and contain only letters";
    private static final String LAST_NAME_ERROR = "Last name must be between 1 and 45 characters and contain only letters";
    private static final String USERNAME_ERROR = "Username must be between 3 and 100 characters and contain only letters or dot";
    private static final String NULL_FIRST_NAME_ERROR = "First name cannot be null or empty";
    private static final String NULL_LAST_NAME_ERROR = "Last name cannot be null or empty";
    private static final String NULL_USERNAME_ERROR = "Username cannot be null or empty";

    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void testValidUserDtoOnCreation() {
        UserDto userDto = createUserDto("Joe", "Doe", null);

        assertNoViolations(userDto, OnCreate.class);
    }

    @Test
    public void testNullFirstName() {
        UserDto userDto = createUserDto(null, "Doe", null);

        assertSingleViolation(userDto, NULL_FIRST_NAME_ERROR, OnCreate.class);
    }

    @Test
    public void testNullLastName() {
        UserDto userDto = createUserDto("Joe", null, null);

        assertSingleViolation(userDto, NULL_LAST_NAME_ERROR, OnCreate.class);
    }

    @Test
    public void testFirstNameTooLong() {
        UserDto userDto = createUserDto("A".repeat(46), "Doe", null);

        assertSingleViolation(userDto, FIRST_NAME_ERROR, OnCreate.class);
    }

    @Test
    public void testLastNameTooLong() {
        UserDto userDto = createUserDto("Joe", "A".repeat(46), null);

        assertSingleViolation(userDto, LAST_NAME_ERROR, OnCreate.class);
    }

    @Test
    public void testFirstNameWithDigits() {
        UserDto userDto = createUserDto("Joe1", "Doe", null);

        assertSingleViolation(userDto, FIRST_NAME_ERROR, OnCreate.class);
    }

    @Test
    public void testLastNameWithDigits() {
        UserDto userDto = createUserDto("Joe", "Doe1", null);

        assertSingleViolation(userDto, LAST_NAME_ERROR, OnCreate.class);
    }

    @Test
    public void testUsernameValidationOnUpdate() {
        UserDto userDto = createUserDto("Joe", "Doe", "Joe.Doe");

        assertNoViolations(userDto, OnUpdate.class);
    }

    @Test
    public void testUsernameValidationOnUpdateNullFirstName() {
        UserDto userDto = createUserDto(null, "Doe", "Joe.Doe");

        assertSingleViolation(userDto, NULL_FIRST_NAME_ERROR, OnUpdate.class);
    }


    @Test
    public void testUsernameValidationOnUpdateNullLastName() {
        UserDto userDto = createUserDto("Joe", null, "Joe.Doe");

        assertSingleViolation(userDto, NULL_LAST_NAME_ERROR, OnUpdate.class);
    }

    @Test
    public void testNullUsernameOnUpdate() {
        UserDto userDto = createUserDto("Joe", "Doe", null);

        assertSingleViolation(userDto, NULL_USERNAME_ERROR, OnUpdate.class);
    }

    @Test
    public void testInvalidUsernameOnUpdate() {
        UserDto userDto = createUserDto("Joe", "Doe", "jd4");

        assertSingleViolation(userDto, USERNAME_ERROR, OnUpdate.class);
    }

    private static UserDto createUserDto(String firstName, String lastName, String username) {
        return UserDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .build();
    }

    private void assertSingleViolation(UserDto dto, String expectedMessage, Class<?> group) {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto, group);
        assertEquals(1, violations.size());
        ConstraintViolation<UserDto> violation = violations.iterator().next();
        assertEquals(expectedMessage, violation.getMessage());
    }

    private void assertNoViolations(UserDto dto, Class<?> group) {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto, group);
        assertEquals(0, violations.size(), "Validation should pass for valid data");
    }
}
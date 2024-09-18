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

public class TrainerDtoTest {

    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void testValidTrainerDtoOnCreate() {
        assertNoViolations(createDto(getUser(), getTrainingType()), OnCreate.class);
    }

    @Test
    public void testValidTrainerDtoOnUpdate() {
        assertNoViolations(createDto(getUserWithUsername(), null), OnUpdate.class);
    }

    @Test
    public void testNullUser() {
        TrainerDto dto = createDto(null, getTrainingType());

        assertSingleViolation(dto, "User cannot be null", OnCreate.class);
    }

    @Test
    public void testNotValidUserNoFirstName() {
        TrainerDto dto = createDto(getUserNoFirstName(), getTrainingType());

        assertSingleViolation(dto, "First name cannot be null", OnCreate.class);
    }

    @Test
    public void testNullTrainingTypeOnCreate() {
        TrainerDto dto = createDto(getUser(), null);

        assertSingleViolation(dto, "TrainingType cannot be null", OnCreate.class);
    }

    @Test
    public void testNullUsernameOnUpdate() {
        TrainerDto dto = createDto(getUser(), getTrainingType());

        assertSingleViolation(dto, "Username cannot be null", OnUpdate.class);
    }

    private static TrainerDto createDto(UserDto user, TrainingTypeDto trainingTypeDto) {
        return TrainerDto.builder()
                .user(user)
                .trainingType(trainingTypeDto)
                .build();
    }

    private static UserDto getUser() {
        return UserDto.builder().firstName("Joe").lastName("Doe").build();
    }

    private static UserDto getUserNoFirstName() {
        return UserDto.builder().lastName("Doe").build();
    }

    private static UserDto getUserWithUsername() {
        return UserDto.builder().firstName("Joe").lastName("Doe").username("Joe.Doe").build();
    }

    private static TrainingTypeDto getTrainingType() {
        return new TrainingTypeDto(1L, "Fitness");
    }

    private void assertSingleViolation(TrainerDto dto, String expectedMessage, Class<?> group) {
        Set<ConstraintViolation<TrainerDto>> violations = validator.validate(dto, group);
        assertEquals(1, violations.size());
        ConstraintViolation<TrainerDto> violation = violations.iterator().next();
        assertEquals(expectedMessage, violation.getMessage());
    }

    private void assertNoViolations(TrainerDto dto, Class<?> group) {
        Set<ConstraintViolation<TrainerDto>> violations = validator.validate(dto, group);
        assertEquals(0, violations.size(), "Validation should pass for valid data");
    }
}
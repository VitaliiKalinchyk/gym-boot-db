package epam.task.gymbootdb.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrainerDtoTest {

    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testValidTrainerDto() {
        assertNoViolations(createDto(getUser(), getTrainingType()));
    }

    @Test
    void testNullUser() {
        TrainerDto dto = createDto(null, getTrainingType());

        assertSingleViolation(dto, "User cannot be null");
    }

    @Test
    void testNotValidUserNoFirstName() {
        TrainerDto dto = createDto(getUserNoFirstName(), getTrainingType());

        assertSingleViolation(dto, "First name cannot be null");
    }

    @Test
    void testNullTrainingType() {
        TrainerDto dto = createDto(getUser(), null);

        assertSingleViolation(dto, "TrainingType cannot be null");
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

    private static TrainingTypeDto getTrainingType() {
        return new TrainingTypeDto(1L, "Fitness");
    }

    private void assertSingleViolation(TrainerDto dto, String expectedMessage) {
        Set<ConstraintViolation<TrainerDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        ConstraintViolation<TrainerDto> violation = violations.iterator().next();
        assertEquals(expectedMessage, violation.getMessage());
    }

    private void assertNoViolations(TrainerDto dto) {
        Set<ConstraintViolation<TrainerDto>> violations = validator.validate(dto);
        assertEquals(0, violations.size(), "Validation should pass for valid data");
    }
}

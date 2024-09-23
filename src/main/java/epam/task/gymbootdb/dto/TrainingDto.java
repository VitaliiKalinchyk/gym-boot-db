package epam.task.gymbootdb.dto;

import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingDto {

    private long id;

    @NotNull(message = "Name cannot be null")
    @Pattern(regexp = "[a-zA-Z0-9 ]{3,45}",
            message = "Name must be between 3 and 45 characters and contain only letters, digits, dots or white spaces")
    private String name;

    @NotNull(message = "Date is required")
    @Future(message = "Date must be in the future")
    private LocalDate date;

    @Min(value = 10, message = "Duration must be greater than 10")
    @Max(value = 300, message = "Duration mustn't be greater than 300")
    private int duration;

    @NotNull(message = "Trainer is required")
    private TrainerDto trainer;

    @NotNull(message = "Trainee is required")
    private TraineeDto trainee;

    @NotNull(message = "TrainingType is required")
    private TrainingTypeDto trainingType;
}

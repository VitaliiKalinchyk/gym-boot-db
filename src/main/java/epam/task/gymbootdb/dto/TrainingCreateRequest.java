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
public class TrainingCreateRequest {

    @NotBlank (message = "Name is required and cannot be blank")
    @Size(min = 2, max = 45, message = "Name must be between 2 and 45 characters")
    private String name;

    @Future(message = "Date must be in the future")
    @NotNull(message = "Date is required")
    private LocalDate date;

    @Min(value = 1, message = "Duration must be greater than 0")
    @Max(value = 300, message = "Duration mustn't be greater than 30")
    private int duration;

    @Positive(message = "Trainer ID must be a positive number")
    private long trainerId;

    @Positive(message = "Trainee ID must be a positive number")
    private long traineeId;

    @Positive(message = "Training Type ID must be a positive number")
    private long trainingTypeId;
}
package epam.task.gymbootdb.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

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
    @Size(min = 2, max = 45, message = "Name must be between 2 and 45 characters")
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
package epam.task.gymbootdb.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraineeTrainingsRequest {

    @NotBlank(message = "Trainee username cannot be null or empty")
    private String traineeUsername;

    private LocalDate fromDate;
    private LocalDate toDate;
    private String trainerUsername;
    private String trainingTypeName;
}
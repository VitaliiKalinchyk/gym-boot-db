package epam.task.gymbootdb.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class TrainerTrainingsRequest {

    @NotBlank(message = "Trainer username cannot be null or empty")
    private String trainerUsername;

    private LocalDate fromDate;
    private LocalDate toDate;
    private String traineeUsername;
}
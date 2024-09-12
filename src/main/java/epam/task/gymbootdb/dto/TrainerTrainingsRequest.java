package epam.task.gymbootdb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class TrainerTrainingsRequest {
    //TODO validation trainerUsername
    private String trainerUsername;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String traineeUsername;
}
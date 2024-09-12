package epam.task.gymbootdb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class TraineeTrainingsRequest {
    //TODO validation traineeUsername
    private String traineeUsername;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String trainerUsername;
    private String trainingTypeName;
}
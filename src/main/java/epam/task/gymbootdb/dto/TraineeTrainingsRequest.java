package epam.task.gymbootdb.dto;

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
    private String username;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Long trainerId;
    private Long trainingTypeId;
}

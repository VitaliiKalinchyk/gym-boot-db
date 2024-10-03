package epam.task.gymbootdb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainerTrainingsRequest {
    private String username;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Long traineeId;
}

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
public class TrainingCreateRequest {
    //TODO validation
    private String name;
    private LocalDate date;
    private int duration;
    private long trainerId;
    private long traineeId;
    private long trainingTypeId;
}
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
public class TrainingResponse {
    private long id;
    private String name;
    private LocalDate date;
    private int duration;
    private TrainerResponse trainer;
    private TraineeResponse trainee;
    private TrainingTypeDto trainingType;
}

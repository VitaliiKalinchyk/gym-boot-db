package epam.task.gymbootdb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraineeWithTrainersResponse {
    private long id;
    private UserResponse user;
    private LocalDate birthday;
    private String address;
    private Set<TrainerResponse> trainers;
}
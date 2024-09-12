package epam.task.gymbootdb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerCreateOrUpdateRequest {
    //TODO validation
    private long id;
    private UserCreateOrUpdateRequest user;
    private TrainingTypeResponse trainingType;
}

package epam.task.gymbootdb.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerCreateOrUpdateRequest {

    private long id;

    @Valid
    @NotNull
    private UserCreateOrUpdateRequest user;

    @NotNull
    private TrainingTypeResponse trainingType;
}

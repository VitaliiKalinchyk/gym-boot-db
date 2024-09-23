package epam.task.gymbootdb.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerDto {

    private long id;

    @Valid
    @NotNull(message = "User cannot be null")
    private UserDto user;

    @NotNull(message = "TrainingType cannot be null")
    private TrainingTypeDto trainingType;

    List<TraineeDto> trainees;
}

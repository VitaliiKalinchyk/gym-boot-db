package epam.task.gymbootdb.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraineeDto {

    private long id;

    @Valid
    @NotNull(message = "User cannot be null")
    private UserDto user;

    @Past(message = "Birthday cannot be in the future")
    private LocalDate birthday;

    @Size(max = 120, message = "Address cannot exceed 120 characters")
    private String address;

    private List<TrainerDto> trainers;
}

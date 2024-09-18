package epam.task.gymbootdb.dto;

import epam.task.gymbootdb.dto.validation.group.OnCreate;
import epam.task.gymbootdb.dto.validation.group.OnUpdate;

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
    @NotNull(message = "User cannot be null", groups = {OnCreate.class, OnUpdate.class})
    private UserDto user;

    @Past(message = "Birthday cannot be in the future", groups = {OnCreate.class, OnUpdate.class})
    private LocalDate birthday;

    @Size(max = 120, message = "Address cannot exceed 120 characters", groups = {OnCreate.class, OnUpdate.class})
    private String address;

    List<TrainerDto> trainers;
}
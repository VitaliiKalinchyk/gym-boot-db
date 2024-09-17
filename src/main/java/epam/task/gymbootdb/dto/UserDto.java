package epam.task.gymbootdb.dto;

import epam.task.gymbootdb.dto.validation.group.OnCreate;
import epam.task.gymbootdb.dto.validation.group.OnUpdate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private long id;

    @NotBlank(message = "First name cannot be null or empty", groups = {OnCreate.class, OnUpdate.class})
    @Pattern(regexp = "[a-zA-Z]{1,45}",
            message = "First name must be between 1 and 45 characters and contain only letters",
            groups = {OnCreate.class, OnUpdate.class})
    private String firstName;

    @NotBlank(message = "Last name cannot be null or empty", groups = {OnCreate.class, OnUpdate.class})
    @Pattern(regexp = "[a-zA-Z]{1,45}",
            message = "Last name must be between 1 and 45 characters and contain only letters",
            groups = {OnCreate.class, OnUpdate.class})
    private String lastName;

    @NotBlank(message = "Username cannot be null or empty", groups = OnUpdate.class)
    @Pattern(regexp = "[a-zA-Z.]{3,100}",
            message = "Username must be between 3 and 100 characters and contain only letters or dot",
            groups = OnUpdate.class)
    private String username;

    private boolean isActive;
}
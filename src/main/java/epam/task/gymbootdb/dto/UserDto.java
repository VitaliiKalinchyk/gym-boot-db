package epam.task.gymbootdb.dto;

import epam.task.gymbootdb.dto.validation.group.OnCreate;
import epam.task.gymbootdb.dto.validation.group.OnUpdate;

import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "First name cannot be null", groups = {OnCreate.class, OnUpdate.class})
    @Pattern(regexp = "[a-zA-Z]{1,45}",
            message = "First name must be between 1 and 45 characters and contain only letters",
            groups = {OnCreate.class, OnUpdate.class})
    private String firstName;

    @NotNull(message = "Last name cannot be null", groups = {OnCreate.class, OnUpdate.class})
    @Pattern(regexp = "[a-zA-Z]{1,45}",
            message = "Last name must be between 1 and 45 characters and contain only letters",
            groups = {OnCreate.class, OnUpdate.class})
    private String lastName;

    @NotNull(message = "Username cannot be null", groups = OnUpdate.class)
    @Pattern(regexp = "[a-zA-Z0-9.]{3,100}",
            message = "Username must be between 3 and 100 characters and contain only only letters, digits, or dot",
            groups = OnUpdate.class)
    private String username;

    private boolean active;
}
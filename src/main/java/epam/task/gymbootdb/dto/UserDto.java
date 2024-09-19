package epam.task.gymbootdb.dto;

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

    @NotNull(message = "First name cannot be null")
    @Pattern(regexp = "[a-zA-Z]{1,45}",
            message = "First name must be between 1 and 45 characters and contain only letters")
    private String firstName;

    @NotNull(message = "Last name cannot be null")
    @Pattern(regexp = "[a-zA-Z]{1,45}",
            message = "Last name must be between 1 and 45 characters and contain only letters")
    private String lastName;

    private String username;
    private boolean active;
}
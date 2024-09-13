package epam.task.gymbootdb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateOrUpdateRequest {

    @NotBlank(message = "First name cannot be null or empty")
    @Size(min = 1, max = 45, message = "First name must be between 1 and 45 characters")
    @Pattern(regexp = "[a-zA-Z]+", message = "First name must contain only letters")
    private String firstName;

    @NotBlank(message = "Last name cannot be null or empty")
    @Size(min = 1, max = 45, message = "Last name must be between 1 and 45 characters")
    @Pattern(regexp = "[a-zA-Z]+", message = "Last name must contain only letters")
    private String lastName;
}
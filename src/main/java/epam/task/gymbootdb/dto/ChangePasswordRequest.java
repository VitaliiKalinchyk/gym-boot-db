package epam.task.gymbootdb.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordRequest {

    @Valid
    @NotNull(message = "User credentials cannot be null")
    private UserCredentials userCredentials;

    @NotBlank(message = "New password cannot be null or empty")
    @Size(min = 8, max = 16, message = "New password must be between 8 and 16 characters")
    private String newPassword;
}
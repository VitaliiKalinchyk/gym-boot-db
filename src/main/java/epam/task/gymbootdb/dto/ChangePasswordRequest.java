package epam.task.gymbootdb.dto;

import jakarta.validation.Valid;
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
    @NotNull(message = "UserCredentials is required")
    UserCredentials userCredentials;

    @NotNull(message = "New password is required")
    @Size(min = 8, max = 16, message = "New password must be between 8 and 16 characters")
    private String newPassword;
}
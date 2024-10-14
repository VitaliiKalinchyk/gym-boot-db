package epam.task.gymbootdb.dto;

import jakarta.validation.Valid;
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
public class ChangePasswordRequest {

    @Valid
    @NotNull(message = "UserCredentials is required")
    private UserCredentials userCredentials;

    @NotNull(message = "New password is required")
    @Pattern(regexp = "[a-zA-Z0-9]{8,16}",
            message = "New password must be between 8 and 16 characters and contain only letters or digits")
    private String newPassword;
}

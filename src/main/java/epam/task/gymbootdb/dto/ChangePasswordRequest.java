package epam.task.gymbootdb.dto;

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

    private long id;

    @NotNull(message = "Old password cannot be null")
    private String oldPassword;

    @NotNull(message = "New password cannot be null")
    @Size(min = 8, max = 16, message = "New password must be between 8 and 16 characters")
    private String newPassword;
}
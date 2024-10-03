package epam.task.gymbootdb.controller;

import epam.task.gymbootdb.dto.ChangePasswordRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/users")
public interface UserController {

    @Operation(summary = "Change active status",
            description = "Changes the active status of a trainee based on the provided ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee status changed successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PatchMapping("/status")
    ResponseEntity<Void> changeActiveStatus(@RequestParam String username);

    @Operation(summary = "Change user password",
            description = "Allows users to change their password after providing the correct credentials.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password format")
    })
    @PutMapping("/change-password")
    ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request);
}

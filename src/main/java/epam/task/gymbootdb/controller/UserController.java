package epam.task.gymbootdb.controller;

import epam.task.gymbootdb.dto.ChangePasswordRequest;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class UserController {

    private final UserService userService;

    @Operation(summary = "User login",
            description = "Validates user credentials and authenticates the user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful authentication"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @GetMapping
    public ResponseEntity<Void> login(@Valid @RequestBody UserCredentials userCredentials){
        userService.matchCredentials(userCredentials);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change user password",
            description = "Allows users to change their password after providing the correct credentials.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password format")
    })
    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);

        return ResponseEntity.ok().build();
    }
}
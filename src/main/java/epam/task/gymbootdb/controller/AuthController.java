package epam.task.gymbootdb.controller;

import epam.task.gymbootdb.dto.JwtTokenDto;
import epam.task.gymbootdb.dto.UserCredentials;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface AuthController {

    @Operation(summary = "User login",
            description = "Authenticates the user and returns a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful, JWT token returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, invalid credentials")
    })
    JwtTokenDto login(UserCredentials credentials);

    @Operation(summary = "User logout",
            description = "Logs out the user and invalidates the JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    String logout(String authorization);
}

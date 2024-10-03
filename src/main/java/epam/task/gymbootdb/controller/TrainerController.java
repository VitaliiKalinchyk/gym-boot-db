package epam.task.gymbootdb.controller;

import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserCredentials;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.http.ResponseEntity;

public interface TrainerController {

    @Operation(summary = "Get trainer by username",
            description = "Fetches the trainer's details based on the provided username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    ResponseEntity<TrainerDto> get(String username);

    @Operation(summary = "Create new trainer",
            description = "Creates a new trainer profile and returns the associated user credentials.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainer created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid trainer data")
    })
    ResponseEntity<UserCredentials> create(TrainerDto trainerDto);

    @Operation(summary = "Update trainer",
            description = "Updates the details of an existing trainer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer updated successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "400", description = "Invalid trainer data")
    })
    ResponseEntity<TrainerDto> update(TrainerDto trainerDto);
}

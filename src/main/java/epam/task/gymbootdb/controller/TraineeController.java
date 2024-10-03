package epam.task.gymbootdb.controller;

import epam.task.gymbootdb.dto.TraineeDto;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserCredentials;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TraineeController {

    @Operation(summary = "Get trainee by username",
            description = "Fetches a trainee's details based on the provided username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    ResponseEntity<TraineeDto> get(String username);

    @Operation(summary = "Create new trainee",
            description = "Creates a new trainee profile and returns the associated user credentials.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainee created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid trainee data")
    })
    ResponseEntity<UserCredentials> create(TraineeDto traineeDto);

    @Operation(summary = "Update trainee",
            description = "Updates the details of an existing trainee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee updated successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "400", description = "Invalid trainee data")
    })
    ResponseEntity<TraineeDto> update(TraineeDto trainee);

    @Operation(summary = "Delete trainee by authentication details",
            description = "Deletes the trainee's profile based on the authentication details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    ResponseEntity<Void> delete();

    @Operation(summary = "Get trainers not assigned to trainee",
            description = "Fetches a list of trainers that are not assigned to the trainee based on the " +
                    "authentication details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainers retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    ResponseEntity<List<TrainerDto>> getTrainersNotAssignedToTrainee();

    @Operation(summary = "Update trainee trainers",
            description = "Updates the list of trainers assigned to a trainee based on the provided trainee and trainer IDs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainers updated successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee or trainer not found")
    })
    ResponseEntity<Void> updateTraineeTrainers(long trainerId);
}

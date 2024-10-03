package epam.task.gymbootdb.controller;

import epam.task.gymbootdb.dto.TraineeDto;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserCredentials;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/trainees")
public interface TraineeController {

    @Operation(summary = "Get trainee by ID",
            description = "Fetches a trainee's details based on the provided ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @GetMapping("/{id}")
    ResponseEntity<TraineeDto> get(@PathVariable long id);

    @Operation(summary = "Create new trainee",
            description = "Creates a new trainee profile and returns the associated user credentials.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainee created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid trainee data")
    })
    @PostMapping
    ResponseEntity<UserCredentials> create(@Valid @RequestBody TraineeDto traineeDto);

    @Operation(summary = "Update trainee by ID",
            description = "Updates the details of an existing trainee based on the provided ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee updated successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "400", description = "Invalid trainee data")
    })
    @PutMapping("/{id}")
    ResponseEntity<TraineeDto> update(@PathVariable long id, @Valid @RequestBody TraineeDto trainee);

    @Operation(summary = "Delete trainee by ID",
            description = "Deletes the trainee's profile based on the provided ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable long id);

    @Operation(summary = "Get trainers not assigned to trainee",
            description = "Fetches a list of trainers that are not assigned to the trainee based on the provided ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainers retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @GetMapping("/{id}/unassigned-trainers")
    ResponseEntity<List<TrainerDto>> getTrainersNotAssignedToTrainee(@PathVariable long id);

    @Operation(summary = "Update trainee trainers",
            description = "Updates the list of trainers assigned to a trainee based on the provided trainee and trainer IDs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainers updated successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee or trainer not found")
    })
    @PutMapping("/{id}/trainers")
    ResponseEntity<Void> updateTraineeTrainers(@PathVariable long id, @RequestBody long trainerId);
}

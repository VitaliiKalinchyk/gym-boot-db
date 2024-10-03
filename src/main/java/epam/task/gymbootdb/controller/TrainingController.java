package epam.task.gymbootdb.controller;

import epam.task.gymbootdb.dto.TrainingDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface TrainingController {

    @Operation(summary = "Create a new training",
            description = "Creates a new training and stores it in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Training created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid training data provided")
    })
    ResponseEntity<Void> create(TrainingDto trainingDto);

    @Operation(summary = "Get trainings for a specific trainee",
            description = "Fetches a list of trainings for a specific trainee, filtered by optional parameters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainings retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    ResponseEntity<List<TrainingDto>> getTraineeTrainings(LocalDate fromDate, LocalDate toDate,
                                                          Long trainerId, Long trainingTypeId);

    @Operation(summary = "Get trainings for a specific trainer",
            description = "Fetches a list of trainings for a specific trainer, filtered by optional parameters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainings retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    ResponseEntity<List<TrainingDto>> getTrainerTrainings(LocalDate fromDate, LocalDate toDate, Long trainerId);
}

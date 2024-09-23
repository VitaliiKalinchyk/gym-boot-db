package epam.task.gymbootdb.controller;

import epam.task.gymbootdb.dto.TrainingDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/trainings")
public interface TrainingController {

    @Operation(summary = "Create a new training",
            description = "Creates a new training and stores it in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Training created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid training data provided")
    })
    @PostMapping
    ResponseEntity<Void> create(@Valid @RequestBody TrainingDto trainingDto);

    @Operation(summary = "Get trainings for a specific trainee",
            description = "Fetches a list of trainings for a specific trainee, filtered by optional parameters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainings retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @GetMapping("/trainee/{id}")
    ResponseEntity<List<TrainingDto>> getTraineeTrainings(
            @PathVariable long id,
            @RequestParam(name = "from-date", required = false) LocalDate fromDate,
            @RequestParam(name = "to-date", required = false) LocalDate toDate,
            @RequestParam(name = "trainer-id", required = false) Long trainerId,
            @RequestParam(name = "training-type-id", required = false) Long trainingTypeId);

    @Operation(summary = "Get trainings for a specific trainer",
            description = "Fetches a list of trainings for a specific trainer, filtered by optional parameters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainings retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @GetMapping("/trainer/{id}")
    ResponseEntity<List<TrainingDto>> getTrainerTrainings(
            @PathVariable long id,
            @RequestParam(name = "from-date", required = false) LocalDate fromDate,
            @RequestParam(name = "to-date", required = false) LocalDate toDate,
            @RequestParam(name = "trainee-id", required = false) Long trainerId);
}

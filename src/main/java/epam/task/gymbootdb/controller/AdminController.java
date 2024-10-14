package epam.task.gymbootdb.controller;

import epam.task.gymbootdb.dto.TraineeDto;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.TrainingDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.time.LocalDate;
import java.util.List;

public interface AdminController {

    @Operation(summary = "Change active status by username",
            description = "Changes the active status of a trainee by username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee status changed successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    void changeActiveStatusForUserByAdmin(String username);

    @Operation(summary = "Update trainee",
            description = "Updates the details of an existing trainee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee updated successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "400", description = "Invalid trainee data")
    })
    TraineeDto updateTraineeProfileByAdmin(String username, TraineeDto trainee);

    @Operation(summary = "Delete trainee by username",
            description = "Deletes the trainee's profile by username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    void deleteTraineeProfileByAdmin(String username);

    @Operation(summary = "Get trainers not assigned to trainee by username",
            description = "Fetches a list of trainers that are not assigned to the trainee by username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainers retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    List<TrainerDto> getTrainersNotAssignedToTraineeByAdmin(String username);

    @Operation(summary = "Update trainee trainers",
            description = "Updates the list of trainers assigned to a trainee based on username and trainer IDs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainers updated successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee or trainer not found")
    })
    void updateTraineeTrainersByAdmin(String username, long trainerId);

    @Operation(summary = "Update trainer",
            description = "Updates the details of an existing trainer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer updated successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "400", description = "Invalid trainer data")
    })
    TrainerDto updateTrainerProfileByAdmin(String username, TrainerDto trainerDto);

    @Operation(summary = "Create a new training",
            description = "Creates a new training and stores it in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Training created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid training data provided")
    })
    void createTrainingFoTraineeByAdmin(String username, TrainingDto trainingDto);

    @Operation(summary = "Get trainings for a specific trainee",
            description = "Fetches a list of trainings for a specific trainee, filtered by optional parameters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainings retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    List<TrainingDto> getTraineeTrainingsByAdmin(String username, LocalDate fromDate, LocalDate toDate,
                                                 Long trainerId, Long trainingTypeId);

    @Operation(summary = "Get trainings for a specific trainer",
            description = "Fetches a list of trainings for a specific trainer, filtered by optional parameters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainings retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    List<TrainingDto> getTrainerTrainingsByAdmin(String username, LocalDate fromDate, LocalDate toDate, Long trainerId);
}

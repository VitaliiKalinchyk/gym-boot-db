package epam.task.gymbootdb.controller;

import epam.task.gymbootdb.dto.TrainingTypeDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

public interface TrainingTypeController {

    @Operation(summary = "Get all training types",
            description = "Retrieves a list of all available training types.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training types retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Server error while fetching training types")
    })
    List<TrainingTypeDto> getAllTrainingTypes();
}

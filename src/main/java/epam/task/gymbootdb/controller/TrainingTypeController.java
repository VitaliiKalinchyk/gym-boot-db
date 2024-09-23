package epam.task.gymbootdb.controller;

import epam.task.gymbootdb.dto.TrainingTypeDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/training-types")
public interface TrainingTypeController {

    @Operation(summary = "Get all training types",
            description = "Retrieves a list of all available training types.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training types retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Server error while fetching training types")
    })
    @GetMapping
    ResponseEntity<List<TrainingTypeDto>> getAll();
}

package epam.task.gymbootdb.controller;

import epam.task.gymbootdb.dto.TrainingTypeDto;
import epam.task.gymbootdb.service.TrainingTypeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/training-types")
@RequiredArgsConstructor
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;

    @Operation(summary = "Get all training types",
            description = "Retrieves a list of all available training types.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training types retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Server error while fetching training types")
    })
    @GetMapping
    public ResponseEntity<List<TrainingTypeDto>> getAll() {
        return ResponseEntity.ok(trainingTypeService.getAll());
    }
}
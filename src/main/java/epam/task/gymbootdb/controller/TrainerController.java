package epam.task.gymbootdb.controller;

import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.service.TrainerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @Operation(summary = "Get trainer by ID",
            description = "Fetches the trainer's details based on the provided ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TrainerDto> get(@PathVariable long id) {
        TrainerDto trainerDto = trainerService.getById(id);

        return ResponseEntity.ok(trainerDto);
    }

    @Operation(summary = "Create new trainer",
            description = "Creates a new trainer profile and returns the associated user credentials.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainer created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid trainer data")
    })
    @PostMapping
    public ResponseEntity<UserCredentials> create(@Valid @RequestBody TrainerDto trainerDto) {
        UserCredentials profile = trainerService.createProfile(trainerDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    @Operation(summary = "Update trainer by ID",
            description = "Updates the details of an existing trainer based on the provided ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer updated successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "400", description = "Invalid trainer data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TrainerDto> update(@PathVariable long id, @Valid @RequestBody TrainerDto trainerDto){
        trainerDto.setId(id);
        TrainerDto update = trainerService.update(trainerDto);

        return ResponseEntity.ok(update);
    }

    @Operation(summary = "Change trainer active status",
            description = "Changes the active status of a trainer based on the provided ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer status changed successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeActiveStatus(@PathVariable long id){
        trainerService.setActiveStatus(id);

        return ResponseEntity.ok().build();
    }
}

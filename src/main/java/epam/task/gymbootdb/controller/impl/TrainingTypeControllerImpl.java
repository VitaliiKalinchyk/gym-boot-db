package epam.task.gymbootdb.controller.impl;

import epam.task.gymbootdb.controller.TrainingTypeController;
import epam.task.gymbootdb.dto.TrainingTypeDto;
import epam.task.gymbootdb.service.TrainingTypeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/training-types")
@RequiredArgsConstructor
@Slf4j
public class TrainingTypeControllerImpl implements TrainingTypeController {

    private static final String TRANSACTION_ID = "transactionId";

    private final TrainingTypeService trainingTypeService;

    @Override
    @GetMapping
    public ResponseEntity<List<TrainingTypeDto>> getAll() {
        log.debug("Training types were fetched. Controller layer. TransactionId: {}", MDC.get(TRANSACTION_ID));

        return ResponseEntity.ok(trainingTypeService.getAll());
    }
}

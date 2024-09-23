package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.TrainingTypeDto;
import epam.task.gymbootdb.dto.mapper.TrainingTypeMapper;
import epam.task.gymbootdb.repository.TrainingTypeRepository;
import epam.task.gymbootdb.service.TrainingTypeService;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingTypeServiceImpl implements TrainingTypeService {

    public static final String TRANSACTION_ID = "transactionId";

    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingTypeMapper trainingTypeMapper;

    @Override
    public List<TrainingTypeDto> getAll() {
        log.debug("TrainingTypes was gotten . Service layer. TransactionId: {}", MDC.get(TRANSACTION_ID));
        return trainingTypeMapper.toDtoList(trainingTypeRepository.findAll());
    }
}

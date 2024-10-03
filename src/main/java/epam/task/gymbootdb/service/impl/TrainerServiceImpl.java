package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserDto;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.dto.mapper.TraineeMapper;
import epam.task.gymbootdb.dto.mapper.TrainerMapper;
import epam.task.gymbootdb.entity.Trainer;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.exception.TrainerException;
import epam.task.gymbootdb.exception.TrainingTypeException;
import epam.task.gymbootdb.repository.TrainerRepository;
import epam.task.gymbootdb.repository.TrainingTypeRepository;
import epam.task.gymbootdb.repository.UserRepository;
import epam.task.gymbootdb.service.TrainerService;
import epam.task.gymbootdb.utils.NameGenerator;
import epam.task.gymbootdb.utils.PasswordGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerServiceImpl implements TrainerService {

    private static final String TRANSACTION_ID = "transactionId";

    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final UserRepository userRepository;
    private final TrainerMapper trainerMapper;
    private final TraineeMapper traineeMapper;
    private final PasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;
    private final NameGenerator nameGenerator;

    @Override
    @Transactional
    public UserCredentials createProfile(TrainerDto request) {
        checkIfTrainingTypeExists(request);

        Trainer entity = trainerMapper.toEntity(request);
        String password = passwordGenerator.generatePassword();
        User user = entity.getUser();
        String username = generateUsername(user);
        setUserFields(user, username, password);

        trainerRepository.save(entity);
        log.debug("Trainer (id = {}, username = {}) was created. Service layer. TransactionId: {}",
                entity.getId(), username, MDC.get(TRANSACTION_ID));

        return new UserCredentials(username, password);
    }

    @Override
    @Transactional
    public TrainerDto update(TrainerDto request) {
        String username = request.getUser().getUsername();
        Trainer entity = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new TrainerException(username));
        updateTrainerFields(request.getUser(), entity.getUser());

        TrainerDto dto = trainerMapper.toDto(trainerRepository.save(entity));
        dto.setTrainees(traineeMapper.toDtoList(entity.getTrainees()));
        log.debug("Trainer (username = {}) was updated. Service layer. TransactionId: {}",
                username, MDC.get(TRANSACTION_ID));

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerDto getByUsername(String username) {
        Trainer entity = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new TrainerException(username));

        TrainerDto dto = trainerMapper.toDto(entity);
        dto.setTrainees(traineeMapper.toDtoList(entity.getTrainees()));
        log.debug("Trainer (username = {}) was fetched. Service layer. TransactionId: {}",
                username, MDC.get(TRANSACTION_ID));

        return dto;
    }

    private void checkIfTrainingTypeExists(TrainerDto request) {
        if (!trainingTypeRepository.existsById(request.getTrainingType().getId())) {
            throw new TrainingTypeException(request.getTrainingType().getId());
        }
    }

    private String generateUsername(User user) {
        String username = nameGenerator.generateUsername(user.getFirstName(), user.getLastName());
        return userRepository.existsByUsername(username) ?
                nameGenerator.generateUsername(username, userRepository.findUsernamesByUsernameStartsWith(username)) :
                username;
    }

    private void setUserFields(User user, String username, String password) {
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);
    }

    private void updateTrainerFields(UserDto userDto, User user) {
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setActive(userDto.isActive());
    }
}

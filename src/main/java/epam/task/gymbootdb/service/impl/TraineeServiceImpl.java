package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.TraineeDto;
import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.dto.mapper.TraineeMapper;
import epam.task.gymbootdb.dto.mapper.TrainerMapper;
import epam.task.gymbootdb.entity.Trainee;
import epam.task.gymbootdb.entity.Trainer;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.exception.TraineeException;
import epam.task.gymbootdb.exception.TrainerException;
import epam.task.gymbootdb.repository.TraineeRepository;
import epam.task.gymbootdb.repository.TrainerRepository;
import epam.task.gymbootdb.repository.UserRepository;
import epam.task.gymbootdb.service.TraineeService;
import epam.task.gymbootdb.utils.NameGenerator;
import epam.task.gymbootdb.utils.PasswordGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TraineeServiceImpl implements TraineeService {

    private static final String TRANSACTION_ID = "transactionId";

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final TraineeMapper traineeMapper;
    private final TrainerMapper trainerMapper;
    private final PasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;
    private final NameGenerator nameGenerator;

    @Override
    @Transactional
    public UserCredentials createProfile(TraineeDto request) {
        Trainee entity = traineeMapper.toEntity(request);
        String password = passwordGenerator.generatePassword();
        User user = entity.getUser();
        String username = generateUsername(user);
        setUserFields(user, username, password);

        traineeRepository.save(entity);
        log.debug("Trainee (id = {}, username = {}) was created. Service layer. TransactionId: {}",
                entity.getId(), username, MDC.get(TRANSACTION_ID));

        return new UserCredentials(username, password);
    }

    @Override
    @Transactional
    public TraineeDto update(TraineeDto request) {
        String username = request.getUser().getUsername();
        Trainee entity = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeException(username));
        updateTraineeFields(request, entity);

        TraineeDto dto = traineeMapper.toDto(traineeRepository.save(entity));
        dto.setTrainers(trainerMapper.toDtoList(entity.getTrainers()));
        log.debug("Trainee (username = {}) was updated. Service layer. TransactionId: {}",
                username, MDC.get(TRANSACTION_ID));

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public TraineeDto getByUsername(String username) {
        Trainee entity = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeException(username));

        TraineeDto dto = traineeMapper.toDto(entity);
        dto.setTrainers(trainerMapper.toDtoList(entity.getTrainers()));
        log.debug("Trainee (username = {}) was fetched. Service layer. TransactionId: {}",
                username, MDC.get(TRANSACTION_ID));

        return dto;
    }

    @Override
    public void deleteByUsername(String username) {
        Trainee entity = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeException(username));
        log.debug("Trainee (username = {}) was deleted. Service layer. TransactionId: {}",
                entity.getId(), MDC.get(TRANSACTION_ID));

        traineeRepository.delete(entity);
    }

    @Override
    public List<TrainerDto> getTrainersNotAssignedToTrainee(String username) {
        if (!traineeRepository.existsByUserUsername(username)) throw new TraineeException(username);
        log.debug("Trainee (username = {}) got unassigned trainers. Service layer. TransactionId: {}",
                username, MDC.get(TRANSACTION_ID));
      
        return trainerMapper.toDtoList(trainerRepository.findTrainersNotAssignedToTrainee(username));
    }

    @Override
    @Transactional
    public void updateTraineeTrainers(String username, long trainerId) {
        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeException(username));
        Trainer trainer = trainerRepository.findById(trainerId).orElseThrow(() -> new TrainerException(trainerId));

        if (!trainee.getTrainers().contains(trainer)) {
            trainee.getTrainers().add(trainer);
            traineeRepository.save(trainee);
            log.debug("Trainee (username = {}) added new trainer to it's list (id = {}). " +
                            "Service layer. TransactionId: {}", username, trainerId, MDC.get(TRANSACTION_ID));
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

    private static void updateTraineeFields(TraineeDto request, Trainee entity) {
        entity.getUser().setFirstName(request.getUser().getFirstName());
        entity.getUser().setLastName(request.getUser().getLastName());
        entity.getUser().setActive(request.getUser().isActive());
        entity.setBirthday(request.getBirthday());
        entity.setAddress(request.getAddress());
    }
}

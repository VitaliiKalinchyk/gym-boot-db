package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserDto;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.dto.mapper.TraineeMapper;
import epam.task.gymbootdb.dto.mapper.TrainerMapper;
import epam.task.gymbootdb.entity.Trainer;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.exception.TrainerException;
import epam.task.gymbootdb.repository.TrainerRepository;
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

    public static final String TRANSACTION_ID = "transactionId";

    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final TrainerMapper trainerMapper;
    private final TraineeMapper traineeMapper;
    private final PasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;
    private final NameGenerator nameGenerator;

    @Override
    @Transactional
    public UserCredentials createProfile(TrainerDto request) {
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
        Trainer entity = trainerRepository.findById(request.getId())
                .orElseThrow(() -> new TrainerException(request.getId()));
        updateTrainerFields(request.getUser(), entity.getUser());

        TrainerDto dto = trainerMapper.toDto(trainerRepository.save(entity));
        dto.setTrainees(traineeMapper.toDtoList(entity.getTrainees()));
        log.debug("Trainer (id = {}) was updated. Service layer. TransactionId: {}",
                entity.getId(), MDC.get(TRANSACTION_ID));

        return dto;
    }

    @Override
    public void setActiveStatus(long id) {
        Trainer entity = trainerRepository.findById(id).orElseThrow(() -> new TrainerException(id));
        boolean status = !entity.getUser().isActive();
        entity.getUser().setActive(status);
        log.debug("Trainee (id = {}) changed status to {}. Service layer. TransactionId: {}",
                id, status, MDC.get(TRANSACTION_ID));

        trainerRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerDto getById(long id) {
        Trainer entity = trainerRepository.findById(id).orElseThrow(() -> new TrainerException(id));

        TrainerDto dto = trainerMapper.toDto(entity);
        dto.setTrainees(traineeMapper.toDtoList(entity.getTrainees()));
        log.debug("Trainer (id = {}) was gotten. Service layer. TransactionId: {}",
                id, MDC.get(TRANSACTION_ID));

        return dto;
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

    private static void updateTrainerFields(UserDto userDto, User user) {
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setActive(userDto.isActive());
    }
}
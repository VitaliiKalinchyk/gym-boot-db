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
import epam.task.gymbootdb.repository.RoleRepository;
import epam.task.gymbootdb.repository.TrainerRepository;
import epam.task.gymbootdb.repository.TrainingTypeRepository;
import epam.task.gymbootdb.repository.UserRepository;
import epam.task.gymbootdb.service.LoggingService;
import epam.task.gymbootdb.service.TrainerService;
import epam.task.gymbootdb.utils.NameGenerator;
import epam.task.gymbootdb.utils.PasswordGenerator;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TrainerMapper trainerMapper;
    private final TraineeMapper traineeMapper;
    private final PasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;
    private final NameGenerator nameGenerator;
    private final LoggingService loggingService;

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
        loggingService.logDebugService("was created", username);

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
        loggingService.logDebugService("was updated", username);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerDto getByUsername(String username) {
        Trainer entity = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new TrainerException(username));

        TrainerDto dto = trainerMapper.toDto(entity);
        dto.setTrainees(traineeMapper.toDtoList(entity.getTrainees()));
        loggingService.logDebugService("was fetched", username);

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
        user.setRoles(Set.of(roleRepository.findByName("ROLE_TRAINER")));
    }

    private void updateTrainerFields(UserDto userDto, User user) {
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setActive(userDto.isActive());
    }
}

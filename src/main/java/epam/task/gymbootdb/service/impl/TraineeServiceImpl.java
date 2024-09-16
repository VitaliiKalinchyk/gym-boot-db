package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.TraineeCreateOrUpdateRequest;
import epam.task.gymbootdb.dto.TraineeResponse;
import epam.task.gymbootdb.dto.TraineeWithTrainersResponse;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.dto.mapper.TraineeMapper;
import epam.task.gymbootdb.entity.Trainee;
import epam.task.gymbootdb.entity.Trainer;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.exception.GymExceptions;
import epam.task.gymbootdb.repository.TraineeRepository;
import epam.task.gymbootdb.repository.TrainerRepository;
import epam.task.gymbootdb.repository.UserRepository;
import epam.task.gymbootdb.service.TraineeService;
import epam.task.gymbootdb.utils.NameGenerator;
import epam.task.gymbootdb.utils.PasswordGenerator;

import static epam.task.gymbootdb.exception.GymExceptions.noSuchTrainee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final TraineeMapper traineeMapper;
    private final PasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;
    private final NameGenerator nameGenerator;

    @Override
    public UserCredentials createProfile(TraineeCreateOrUpdateRequest request) {
        Trainee entity = traineeMapper.toEntity(request);
        String password = passwordGenerator.generatePassword();
        User user = entity.getUser();
        String username = generateUsername(user);
        setUserFields(user, username, password);

        traineeRepository.save(entity);

        return new UserCredentials(username, password);
    }

    @Override
    public boolean matchCredentials(UserCredentials user) {
        Optional<Trainee> entity = traineeRepository.findByUserUsername(user.getUsername());

        return entity.isPresent() && passwordEncoder.matches(user.getPassword(), entity.get().getUser().getPassword());
    }

    @Override
    public TraineeResponse update(TraineeCreateOrUpdateRequest request) {
        Trainee entity = traineeRepository.findById(request.getId()).orElseThrow(() -> noSuchTrainee(request.getId()));
        updateTraineeFields(request, entity);

        return traineeMapper.toDto(traineeRepository.save(entity));
    }

    @Override
    public void changePassword(UserCredentials user, String newPassword) {
        Trainee entity = traineeRepository.findByUserUsername(user.getUsername())
                                          .orElseThrow(() -> noSuchTrainee(user.getUsername()));
        if (!passwordEncoder.matches(user.getPassword(), entity.getUser().getPassword())) {
            throw  GymExceptions.wrongPassword();
        }
        entity.getUser().setPassword(passwordEncoder.encode(newPassword));

        traineeRepository.save(entity);
    }

    @Override
    public void setActiveStatus(String username, boolean isActive) {
        Trainee entity = traineeRepository.findByUserUsername(username).orElseThrow(() -> noSuchTrainee(username));
        entity.getUser().setActive(isActive);

        traineeRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public TraineeResponse getById(long id) {
        Trainee entity = traineeRepository.findById(id).orElseThrow(() -> noSuchTrainee(id));

        return traineeMapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public TraineeWithTrainersResponse getByUsername(String username) {
        Trainee entity = traineeRepository.findByUserUsername(username).orElseThrow(() -> noSuchTrainee(username));

        return traineeMapper.toDtoWithTrainers(entity);
    }

    @Override
    public void deleteByUsername(String username) {
        Trainee entity = traineeRepository.findByUserUsername(username).orElseThrow(() -> noSuchTrainee(username));

        traineeRepository.delete(entity);
    }

    @Override
    public void updateTraineeTrainers(String username, List<Long> trainerIds) {
        Trainee entity = traineeRepository.findByUserUsername(username).orElseThrow(() -> noSuchTrainee(username));
        Set<Trainer> trainersByIds = Set.copyOf(trainerRepository.findAllById(trainerIds));
        entity.getTrainers().addAll(trainersByIds);

        traineeRepository.save(entity);
    }


    private void setUserFields(User user, String username, String password) {
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);
    }


    private static void updateTraineeFields(TraineeCreateOrUpdateRequest request, Trainee entity) {
        entity.getUser().setFirstName(request.getUser().getFirstName());
        entity.getUser().setLastName(request.getUser().getLastName());
        entity.setBirthday(request.getBirthday());
        entity.setAddress(request.getAddress());
    }

    private String generateUsername(User user) {
        String username = nameGenerator.generateUsername(user.getFirstName(), user.getLastName());

        return userRepository.existsByUsername(username) ?
                nameGenerator.generateUsername(username, userRepository.findUsernamesByUsernameStartsWith(username)) :
                username;
    }
}
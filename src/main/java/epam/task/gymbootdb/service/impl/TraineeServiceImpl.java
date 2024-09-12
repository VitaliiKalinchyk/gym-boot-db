package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.TraineeCreateOrUpdateRequest;
import epam.task.gymbootdb.dto.TraineeResponse;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.dto.mapper.TraineeMapper;
import epam.task.gymbootdb.entity.Trainee;
import epam.task.gymbootdb.entity.Trainer;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.repository.TraineeRepository;
import epam.task.gymbootdb.repository.TrainerRepository;
import epam.task.gymbootdb.repository.UserRepository;
import epam.task.gymbootdb.service.TraineeService;
import epam.task.gymbootdb.utils.NameGenerator;
import epam.task.gymbootdb.utils.PasswordGenerator;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
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
    public UserCredentials createProfile(TraineeCreateOrUpdateRequest request) {  //TODO nonnull
        Trainee entity = traineeMapper.toEntity(request);
        String password = passwordGenerator.generatePassword();
        User user = entity.getUser();
        String username = generateUsername(user);
        setUserFields(user, username, password);

        traineeRepository.save(entity);

        return new UserCredentials(username, password);
    }

    private void setUserFields(User user, String username, String password) {
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);
    }

    @Override
    public boolean matchCredentials(UserCredentials user) {  //TODO nonnull
        Optional<Trainee> entity = traineeRepository.findByUserUsername(user.getUsername());

        return entity.isPresent() && passwordEncoder.matches(user.getPassword(), entity.get().getUser().getPassword());
    }

    @Override
    public TraineeResponse update(TraineeCreateOrUpdateRequest request) {  //TODO nonnull
        //TODO custom exceptions
        Trainee entity = traineeRepository.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("Trainee with id = " + request.getId() + " not found"));
        updateTraineeFields(request, entity);

        return traineeMapper.toDto(traineeRepository.save(entity));
    }

    private static void updateTraineeFields(TraineeCreateOrUpdateRequest request, Trainee entity) {
        entity.getUser().setFirstName(request.getUser().getFirstName());
        entity.getUser().setLastName(request.getUser().getLastName());
        entity.setBirthday(request.getBirthday());
        entity.setAddress(request.getAddress());
    }

    @Override
    public void changePassword(UserCredentials user) {  //TODO nonnull
        //TODO custom exceptions
        String username = user.getUsername();
        Trainee entity = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee with username = " + username + " not found"));
        entity.getUser().setPassword(passwordEncoder.encode(user.getPassword()));

        traineeRepository.save(entity);
    }

    @Override
    public void setActiveStatus(String username, boolean isActive) {  //TODO nonnull
        //TODO custom exceptions
        Trainee entity = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee with username = " + username + " not found"));
        entity.getUser().setActive(isActive);

        traineeRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public TraineeResponse getById(long id) {
        //TODO custom exceptions
        Trainee entity = traineeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trainee with id = " + id + " not found"));

        return traineeMapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public TraineeResponse getByUsername(String username) {  //TODO nonnull
        //TODO custom exceptions
        Trainee entity = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee with username = " + username + " not found"));

        return traineeMapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TraineeResponse> getAll() {
        return traineeMapper.toDtoList(traineeRepository.findAll());
    }

    @Override
    public void delete(long id) {
        //TODO custom exceptions
        boolean traineeExists = traineeRepository.existsById(id);
        if (!traineeExists) throw  new EntityNotFoundException("Trainee with id = " + id + " not found");

        traineeRepository.deleteById(id);
    }

    @Override
    public void deleteByUsername(String username) {  //TODO nonnull
        //TODO custom exceptions
        boolean traineeExists = traineeRepository.existsByUserUsername(username);
        if (!traineeExists) throw  new EntityNotFoundException("Trainee with username = " + username + " not found");

        traineeRepository.deleteByUserUsername(username);
    }

    @Override
    public void updateTraineeTrainers(String traineeUsername, List<Long> trainerIds) { //TODO nonnull
        //TODO custom exceptions
        Trainee entity = traineeRepository.findByUserUsername(traineeUsername)
                .orElseThrow(() -> new EntityNotFoundException("Trainee with username = " + traineeUsername + " not found"));
        Set<Trainer> trainersByIds = new HashSet<>(trainerRepository.findAllById(trainerIds));
        entity.getTrainers().addAll(trainersByIds);

        traineeRepository.save(entity);
    }

    private String generateUsername(User user) {  //TODO nonnull
        String username = nameGenerator.generateUsername(user);

        return userRepository.existsByUsername(username) ?
                nameGenerator.generateUsername(username, userRepository.findUsernamesByUsernameStartsWith(username)) :
                username;
    }
}
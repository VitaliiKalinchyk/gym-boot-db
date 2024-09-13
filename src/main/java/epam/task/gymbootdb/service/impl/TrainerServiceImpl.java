package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.TrainerCreateOrUpdateRequest;
import epam.task.gymbootdb.dto.TrainerResponse;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.dto.mapper.TrainerMapper;
import epam.task.gymbootdb.entity.Trainer;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.repository.TraineeRepository;
import epam.task.gymbootdb.repository.TrainerRepository;
import epam.task.gymbootdb.repository.UserRepository;
import epam.task.gymbootdb.service.TrainerService;
import epam.task.gymbootdb.utils.NameGenerator;
import epam.task.gymbootdb.utils.PasswordGenerator;

import static epam.task.gymbootdb.exception.GymExceptions.noSuchTrainer;
import static epam.task.gymbootdb.exception.GymExceptions.noSuchTrainee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final UserRepository userRepository;
    private final TrainerMapper trainerMapper;
    private final PasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;
    private final NameGenerator nameGenerator;

    @Override
    public UserCredentials createProfile(TrainerCreateOrUpdateRequest request) {
        Trainer entity = trainerMapper.toEntity(request);
        String password = passwordGenerator.generatePassword();
        User user = entity.getUser();
        String username = generateUsername(user);
        setUserFields(user, username, password);

        trainerRepository.save(entity);

        return new UserCredentials(username, password);
    }

    private void setUserFields(User user, String username, String password) {
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);
    }

    @Override
    public boolean matchCredentials(UserCredentials user) {
        Optional<Trainer> entity = trainerRepository.findByUserUsername(user.getUsername());

        return entity.isPresent() && passwordEncoder.matches(user.getPassword(), entity.get().getUser().getPassword());
    }

    @Override
    public TrainerResponse update(TrainerCreateOrUpdateRequest request) {
        Trainer entity = trainerRepository.findById(request.getId()).orElseThrow(() -> noSuchTrainer(request.getId()));
        User user = entity.getUser();
        user.setFirstName(request.getUser().getFirstName());
        user.setLastName(request.getUser().getLastName());

        return trainerMapper.toDto(trainerRepository.save(entity));
    }

    @Override
    public void changePassword(UserCredentials user) {
        String username = user.getUsername();
        Trainer entity = trainerRepository.findByUserUsername(username).orElseThrow(() -> noSuchTrainer(username));
        entity.getUser().setPassword(passwordEncoder.encode(user.getPassword()));

        trainerRepository.save(entity);
    }

    @Override
    public void setActiveStatus(String username, boolean isActive) {
        Trainer entity = trainerRepository.findByUserUsername(username).orElseThrow(() -> noSuchTrainer(username));
        entity.getUser().setActive(isActive);

        trainerRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerResponse getById(long id) {
        Trainer entity = trainerRepository.findById(id).orElseThrow(() -> noSuchTrainer(id));

        return trainerMapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerResponse getByUsername(String username) {
        Trainer entity = trainerRepository.findByUserUsername(username).orElseThrow(() -> noSuchTrainer(username));

        return trainerMapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerResponse> getAll() {
        return trainerMapper.toDtoList(trainerRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerResponse> getTrainersNotAssignedToTrainee(String traineeUsername) {
        if (!traineeRepository.existsByUserUsername(traineeUsername)) throw noSuchTrainee(traineeUsername);

        return trainerMapper.toDtoList(trainerRepository.findTrainersNotAssignedToTrainee(traineeUsername));
    }

    private String generateUsername(User user) {
        String username = nameGenerator.generateUsername(user.getFirstName(), user.getLastName());

        return userRepository.existsByUsername(username) ?
                nameGenerator.generateUsername(username, userRepository.findUsernamesByUsernameStartsWith(username)) :
                username;
    }
}
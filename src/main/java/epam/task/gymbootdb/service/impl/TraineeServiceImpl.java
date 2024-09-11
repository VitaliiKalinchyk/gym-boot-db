package epam.task.gymbootdb.service.impl;

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
    private final PasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;
    private final NameGenerator nameGenerator;

    @Override
    public Trainee createProfile(Trainee trainee) {
        String password = passwordGenerator.generatePassword();
        User user = trainee.getUser();
        user.setUsername(generateUsername(user));
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);
//TODO DTO
// повернути реальний пароль

        return traineeRepository.save(trainee);
    }

    @Override
    public boolean matchCredentials(String username, String password) {
        //TODO DTO
        Optional<Trainee> trainee = traineeRepository.findByUserUsername(username);

        return trainee.isPresent() && passwordEncoder.matches(password, trainee.get().getUser().getPassword());
    }

    @Override
    public Trainee update(Trainee trainee) {
        //TODO DTO
        Trainee existingTrainee = getById(trainee.getId());
        User existingUser = existingTrainee.getUser();
        existingUser.setFirstName(trainee.getUser().getFirstName());
        existingUser.setLastName(trainee.getUser().getLastName());
        existingTrainee.setBirthday(trainee.getBirthday());
        existingTrainee.setAddress(trainee.getAddress());

        return traineeRepository.save(existingTrainee);
    }

    @Override
    public void changePassword(String username, String newPassword) {
        //TODO DTO
        Trainee trainee = getByUsername(username);
        trainee.getUser().setPassword(passwordEncoder.encode(newPassword));

        traineeRepository.save(trainee);
    }

    @Override
    public void setActiveStatus(String username, boolean isActive) {
        Trainee trainee = getByUsername(username);
        trainee.getUser().setActive(isActive);

        traineeRepository.save(trainee);
    }

    @Override
    @Transactional(readOnly = true)
    public Trainee getById(long id) {
        return traineeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Trainee getByUsername(String username) {
        return traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainee> getAll() {
        return traineeRepository.findAll();
    }

    @Override
    public void delete(long id) {
        traineeRepository.delete(getById(id));
    }

    @Override
    public void deleteByUsername(String username) {
        traineeRepository.delete(getByUsername(username));
    }

    @Override
    public Trainee updateTraineeTrainers(String traineeUsername, List<Long> trainerIds) {
        Trainee trainee = getByUsername(traineeUsername);
        Set<Trainer> trainersByIds = new HashSet<>(trainerRepository.findAllById(trainerIds));
        trainee.getTrainers().addAll(trainersByIds);

        return traineeRepository.save(trainee);
    }

    private String generateUsername(User user) {
        String username = nameGenerator.generateUsername(user);

        return userRepository.existsByUsername(username) ?
                nameGenerator.generateUsername(username, userRepository.findUsernamesByUsernameStartsWith(username)) :
                username;
    }
}
package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.entity.Trainer;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.repository.TrainerRepository;
import epam.task.gymbootdb.repository.UserRepository;
import epam.task.gymbootdb.service.TrainerService;
import epam.task.gymbootdb.utils.NameGenerator;
import epam.task.gymbootdb.utils.PasswordGenerator;

import jakarta.persistence.EntityNotFoundException;
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
    private final UserRepository userRepository;
    private final PasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;
    private final NameGenerator nameGenerator;

    @Override
    public Trainer createProfile(Trainer trainer) {
        String password = passwordGenerator.generatePassword();
        User user = trainer.getUser();
        user.setUsername(generateUsername(user));
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);
//TODO DTO
// повернути реальний пароль

        return trainerRepository.save(trainer);
    }

    @Override
    public boolean matchCredentials(String username, String password) {
        //TODO DTO
        Optional<Trainer> trainer = trainerRepository.findByUserUsername(username);

        return trainer.isPresent() && passwordEncoder.matches(password, trainer.get().getUser().getPassword());
    }

    @Override
    public Trainer update(Trainer trainer) {
        //TODO DTO
        Trainer existingTrainer = trainerRepository.findById(trainer.getId()).orElseThrow();
        User existingUser = existingTrainer.getUser();
        existingUser.setFirstName(trainer.getUser().getFirstName());
        existingUser.setLastName(trainer.getUser().getLastName());

        return trainerRepository.save(existingTrainer);
    }

    @Override
    public void changePassword(String username, String newPassword) {
        //TODO DTO
        Trainer trainer = trainerRepository.findByUserUsername(username).orElseThrow();
        trainer.getUser().setPassword(passwordEncoder.encode(newPassword));

        trainerRepository.save(trainer);
    }

    @Override
    public void setActiveStatus(String username, boolean isActive) {
        Trainer trainer = trainerRepository.findByUserUsername(username).orElseThrow();
        trainer.getUser().setActive(isActive);

        trainerRepository.save(trainer);
    }

    @Override
    @Transactional(readOnly = true)
    public Trainer getById(long id) {
        return trainerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Trainer getByUsername(String username) {
        return trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> getAll() {
        return trainerRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> getTrainersNotAssignedToTrainee(String traineeUsername) {
        return trainerRepository.findTrainersNotAssignedToTrainee(traineeUsername);
    }

    private String generateUsername(User user) {
        String username = nameGenerator.generateUsername(user);

        return userRepository.existsByUsername(username) ?
                nameGenerator.generateUsername(username, userRepository.findUsernamesByUsernameStartsWith(username)) :
                username;
    }
}
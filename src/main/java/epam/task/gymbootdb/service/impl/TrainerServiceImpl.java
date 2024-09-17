package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.ChangePasswordRequest;
import epam.task.gymbootdb.dto.TrainerCreateOrUpdateRequest;
import epam.task.gymbootdb.dto.TrainerResponse;
import epam.task.gymbootdb.dto.UserCredentials;
import epam.task.gymbootdb.dto.mapper.TrainerMapper;
import epam.task.gymbootdb.entity.Trainer;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.exception.PasswordException;
import epam.task.gymbootdb.exception.TraineeException;
import epam.task.gymbootdb.exception.TrainerException;
import epam.task.gymbootdb.repository.TraineeRepository;
import epam.task.gymbootdb.repository.TrainerRepository;
import epam.task.gymbootdb.repository.UserRepository;
import epam.task.gymbootdb.service.TrainerService;
import epam.task.gymbootdb.utils.NameGenerator;
import epam.task.gymbootdb.utils.PasswordGenerator;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final UserRepository userRepository;
    private final TrainerMapper trainerMapper;
    private final PasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;
    private final NameGenerator nameGenerator;

    @Override
    @Transactional
    public UserCredentials createProfile(TrainerCreateOrUpdateRequest request) {
        Trainer entity = trainerMapper.toEntity(request);
        String password = passwordGenerator.generatePassword();
        User user = entity.getUser();
        String username = generateUsername(user);
        setUserFields(user, username, password);

        trainerRepository.save(entity);

        return new UserCredentials(username, password);
    }

    @Override
    public void matchCredentials(UserCredentials user) {
        Trainer entity = trainerRepository.findByUserUsername(user.getUsername())
                .orElseThrow(() -> new TrainerException(user.getUsername()));

        if (!passwordEncoder.matches(user.getPassword(), entity.getUser().getPassword())) {
            throw new PasswordException();
        }
    }

    @Override
    @Transactional
    public TrainerResponse update(TrainerCreateOrUpdateRequest request) {
        long id = request.getId();
        Trainer entity = trainerRepository.findById(id).orElseThrow(() -> new TrainerException(id));
        User user = entity.getUser();
        user.setFirstName(request.getUser().getFirstName());
        user.setLastName(request.getUser().getLastName());

        return trainerMapper.toDto(trainerRepository.save(entity));
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        UserCredentials userCredentials = request.getUserCredentials();
        String username = userCredentials.getUsername();
        Trainer entity = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new TrainerException(username));
        if (!passwordEncoder.matches(userCredentials.getPassword(), entity.getUser().getPassword())) {
            throw  new PasswordException();
        }
        entity.getUser().setPassword(passwordEncoder.encode(request.getNewPassword()));

        trainerRepository.save(entity);
    }

    @Override
    public void setActiveStatus(long id, boolean isActive) {
        Trainer entity = trainerRepository.findById(id).orElseThrow(() -> new TrainerException(id));
        entity.getUser().setActive(isActive);

        trainerRepository.save(entity);
    }

    @Override
    public TrainerResponse getById(long id) {
        Trainer entity = trainerRepository.findById(id).orElseThrow(() -> new TrainerException(id));

        return trainerMapper.toDto(entity);
    }

    @Override
    public List<TrainerResponse> getTrainersNotAssignedToTrainee(long id) {
        if (!traineeRepository.existsById(id)) throw new TraineeException(id);

        return trainerMapper.toDtoList(trainerRepository.findTrainersNotAssignedToTrainee(id));
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
}
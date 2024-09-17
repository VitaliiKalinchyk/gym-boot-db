package epam.task.gymbootdb.service.impl;

import epam.task.gymbootdb.dto.*;
import epam.task.gymbootdb.dto.mapper.TraineeMapper;
import epam.task.gymbootdb.entity.Trainee;
import epam.task.gymbootdb.entity.Trainer;
import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.exception.PasswordException;
import epam.task.gymbootdb.exception.TraineeException;
import epam.task.gymbootdb.exception.TrainerException;
import epam.task.gymbootdb.repository.TraineeRepository;
import epam.task.gymbootdb.repository.TrainerRepository;
import epam.task.gymbootdb.repository.UserRepository;
import epam.task.gymbootdb.service.TraineeService;
import epam.task.gymbootdb.utils.NameGenerator;
import epam.task.gymbootdb.utils.PasswordGenerator;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final TraineeMapper traineeMapper;
    private final PasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;
    private final NameGenerator nameGenerator;

    @Override
    @Transactional
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
    public void matchCredentials(UserCredentials user) {
        Trainee entity = traineeRepository.findByUserUsername(user.getUsername())
                .orElseThrow(() -> new TraineeException(user.getUsername()));

        if (!passwordEncoder.matches(user.getPassword(), entity.getUser().getPassword())) {
            throw new PasswordException();
        }
    }

    @Override
    @Transactional
    public TraineeResponse update(TraineeCreateOrUpdateRequest request) {
        long id = request.getId();
        Trainee entity = traineeRepository.findById(id).orElseThrow(() -> new TraineeException(id));
        updateTraineeFields(request, entity);

        return traineeMapper.toDto(traineeRepository.save(entity));
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        UserCredentials userCredentials = request.getUserCredentials();
        String username = userCredentials.getUsername();
        Trainee entity = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new TraineeException(username));
        if (!passwordEncoder.matches(userCredentials.getPassword(), entity.getUser().getPassword())) {
            throw  new PasswordException();
        }
        entity.getUser().setPassword(passwordEncoder.encode(request.getNewPassword()));

        traineeRepository.save(entity);
    }

    @Override
    public void setActiveStatus(long id, boolean isActive) {
        Trainee entity = traineeRepository.findById(id).orElseThrow(() -> new TraineeException(id));
        entity.getUser().setActive(isActive);

        traineeRepository.save(entity);
    }

    @Override
    public TraineeResponse getById(long id) {
        Trainee entity = traineeRepository.findById(id).orElseThrow(() -> new TraineeException(id));

        return traineeMapper.toDto(entity);
    }

    @Override
    public void deleteById(long id) {
        Trainee entity = traineeRepository.findById(id)
                .orElseThrow(() -> new TraineeException(id));

        traineeRepository.delete(entity);
    }

    @Override
    public void updateTraineeTrainers(long traineeId, long trainerId) {
        Trainee trainee = traineeRepository.findById(traineeId).orElseThrow(() -> new TraineeException(traineeId));
        Trainer trainer = trainerRepository.findById(trainerId).orElseThrow(() -> new TrainerException(trainerId));
        trainee.getTrainers().add(trainer);

        traineeRepository.save(trainee);
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
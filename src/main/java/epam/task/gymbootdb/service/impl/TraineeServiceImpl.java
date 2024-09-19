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

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {

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

        return new UserCredentials(username, password);
    }

    @Override
    @Transactional
    public TraineeDto update(TraineeDto request) {
        Trainee entity = traineeRepository.findById(request.getId())
                .orElseThrow(() -> new TraineeException(request.getId()));
        updateTraineeFields(request, entity);

        TraineeDto dto = traineeMapper.toDto(traineeRepository.save(entity));
        dto.setTrainers(trainerMapper.toDtoList(entity.getTrainers()));

        return dto;
    }

    @Override
    public void setActiveStatus(long id, boolean isActive) {
        Trainee entity = traineeRepository.findById(id).orElseThrow(() -> new TraineeException(id));
        entity.getUser().setActive(isActive);

        traineeRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public TraineeDto getById(long id) {
        Trainee entity = traineeRepository.findById(id).orElseThrow(() -> new TraineeException(id));

        TraineeDto dto = traineeMapper.toDto(entity);
        dto.setTrainers(trainerMapper.toDtoList(entity.getTrainers()));

        return dto;
    }

    @Override
    public void deleteById(long id) {
        Trainee entity = traineeRepository.findById(id).orElseThrow(() -> new TraineeException(id));

        traineeRepository.delete(entity);
    }

    @Override
    public List<TrainerDto> getTrainersNotAssignedToTrainee(long id) {
        if (!traineeRepository.existsById(id)) throw new TraineeException(id);

        return trainerMapper.toDtoList(trainerRepository.findTrainersNotAssignedToTrainee(id));
    }

    @Override
    @Transactional
    public void updateTraineeTrainers(long traineeId, long trainerId) {
        Trainee trainee = traineeRepository.findById(traineeId).orElseThrow(() -> new TraineeException(traineeId));
        Trainer trainer = trainerRepository.findById(trainerId).orElseThrow(() -> new TrainerException(trainerId));

        if (!trainee.getTrainers().contains(trainer)) {
            trainee.getTrainers().add(trainer);
            traineeRepository.save(trainee);
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
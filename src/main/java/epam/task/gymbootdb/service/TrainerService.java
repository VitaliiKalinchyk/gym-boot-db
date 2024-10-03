package epam.task.gymbootdb.service;

import epam.task.gymbootdb.dto.TrainerDto;
import epam.task.gymbootdb.dto.UserCredentials;

public interface TrainerService {

    UserCredentials createProfile(TrainerDto trainer);

    TrainerDto update(TrainerDto trainer);

    TrainerDto getByUsername(String username);
}

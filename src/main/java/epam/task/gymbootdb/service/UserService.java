package epam.task.gymbootdb.service;

import epam.task.gymbootdb.dto.ChangePasswordRequest;
import epam.task.gymbootdb.dto.UserCredentials;

public interface UserService {

    void matchCredentials(UserCredentials user);

    void changePassword(ChangePasswordRequest request);
}
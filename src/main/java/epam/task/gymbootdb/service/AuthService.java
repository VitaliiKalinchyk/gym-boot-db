package epam.task.gymbootdb.service;

import epam.task.gymbootdb.dto.UserCredentials;

public interface AuthService {
    String authenticate(UserCredentials credentials);
}

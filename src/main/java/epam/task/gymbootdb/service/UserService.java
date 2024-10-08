package epam.task.gymbootdb.service;

import epam.task.gymbootdb.dto.ChangePasswordRequest;

public interface UserService {

    void changeStatus(String username);

    void changePassword(ChangePasswordRequest request);

    void createAdmin(String adminName, String adminPassword);
}

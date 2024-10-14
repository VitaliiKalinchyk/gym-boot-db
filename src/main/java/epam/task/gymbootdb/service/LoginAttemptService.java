package epam.task.gymbootdb.service;

public interface LoginAttemptService {

    boolean isBlocked(String username);

    void loginSucceeded(String username);

    void loginFailed(String username);

    void removeExpiredBlocks();
}

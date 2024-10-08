package epam.task.gymbootdb.utils;

public interface LoginAttemptUtil {
    boolean isBlocked(String username);

    void loginSucceeded(String username);

    void loginFailed(String username);
}

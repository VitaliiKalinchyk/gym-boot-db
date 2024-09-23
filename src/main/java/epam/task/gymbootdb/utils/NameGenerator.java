package epam.task.gymbootdb.utils;

import java.util.List;

public interface NameGenerator {
    String generateUsername(String firstName, String lastName);

    String generateUsername(String username, List<String> existingUsernames);
}

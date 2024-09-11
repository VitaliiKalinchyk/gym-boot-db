package epam.task.gymbootdb.utils;

import epam.task.gymbootdb.entity.User;

import java.util.List;

public interface NameGenerator {

    String generateUsername(User user);

    String generateUsername(String username, List<String> existingUsername);

}
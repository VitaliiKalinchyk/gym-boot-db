package epam.task.gymbootdb.utils.impl;

import epam.task.gymbootdb.utils.NameGenerator;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.ToIntFunction;

@Component
public class NameGeneratorImpl implements NameGenerator {
    @Override
    public String generateUsername(String firstName,String lastName) {
        return firstName + "." + lastName;
    }

    @Override
    public String generateUsername(String username, List<String> existingUsernames) {
        ToIntFunction<String> stringToIntFunction = i -> {
            try {
                return Integer.parseInt(i);
            } catch (NumberFormatException e) {
                return 0;
            }
        };

        return username + (getMaxIndex(username, existingUsernames, stringToIntFunction) + 1);
    }

    private int getMaxIndex(String username,
                            List<String> existingUsernames,
                            ToIntFunction<String> stringToIntFunction) {
        return existingUsernames.stream()
                .filter(u -> u.startsWith(username))
                .map(u -> u.substring(username.length()))
                .filter(s -> !s.isEmpty())
                .mapToInt(stringToIntFunction)
                .max()
                .orElse(0);
    }
}

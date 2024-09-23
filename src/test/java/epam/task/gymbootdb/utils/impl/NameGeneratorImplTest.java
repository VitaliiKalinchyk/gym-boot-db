package epam.task.gymbootdb.utils.impl;

import epam.task.gymbootdb.utils.NameGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NameGeneratorImplTest {

    private final NameGenerator nameGenerator = new NameGeneratorImpl();

    @Test
    void testGenerateUsernameWithoutExistingUsernames() {
        String generatedUsername = nameGenerator.generateUsername("John", "Doe");

        assertEquals("John.Doe", generatedUsername);
    }

    @Test
    void testGenerateUsernameWithExistingUsernames() {
        List<String> existingUsernames = List.of("John.Doe1", "John.Doe2");

        String generatedUsername = nameGenerator.generateUsername("John.Doe", existingUsernames);

        assertEquals("John.Doe3", generatedUsername);
    }

    @Test
    void testGenerateUsernameWithSimilarUsernames() {
        List<String> existingUsernames = List.of("John.Doe1", "John.Doe2", "john.doe7", "John.Doer5", "aJohn.Doer9");

        String generatedUsername = nameGenerator.generateUsername("John.Doe", existingUsernames);

        assertEquals("John.Doe3", generatedUsername);
    }

    @Test
    void testGenerateUsernameWithNoExistingIndexes() {
        List<String> existingUsernames = List.of("John.Doe");

        String generatedUsername = nameGenerator.generateUsername("John.Doe", existingUsernames);

        assertEquals("John.Doe1", generatedUsername);
    }
}

package epam.task.gymbootdb.utils.impl;

import epam.task.gymbootdb.entity.User;
import epam.task.gymbootdb.utils.NameGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NameGeneratorImplTest {

    private final NameGenerator nameGenerator = new NameGeneratorImpl();

    @Test
    public void testGenerateUsernameWithoutExistingUsernames() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");

        String generatedUsername = nameGenerator.generateUsername(user);

        assertEquals("John.Doe", generatedUsername);
    }

    @Test
    public void testGenerateUsernameUserIsNull() {
        assertThrows(NullPointerException.class, () -> nameGenerator.generateUsername(null));
    }

    @Test
    public void testGenerateUsernameFirstNameIsNull() {
        User user = new User();

        NullPointerException e = assertThrows(NullPointerException.class, () -> nameGenerator.generateUsername(user));

        assertEquals("First name is null", e.getMessage());
    }

    @Test
    public void testGenerateUsernameLastNameIsNull() {
        User user = new User();
        user.setFirstName("John");

        NullPointerException e = assertThrows(NullPointerException.class, () -> nameGenerator.generateUsername(user));

        assertEquals("Last name is null", e.getMessage());
    }

    @Test
    public void testGenerateUsernameWithExistingUsernames() {
        List<String> existingUsernames = List.of("John.Doe1", "John.Doe2");

        String generatedUsername = nameGenerator.generateUsername("John.Doe", existingUsernames);

        assertEquals("John.Doe3", generatedUsername);
    }

    @Test
    public void testGenerateUsernameWithSimilarUsernames() {
        List<String> existingUsernames = List.of("John.Doe1", "John.Doe2", "john.doe7", "John.Doer5", "aJohn.Doer9");

        String generatedUsername = nameGenerator.generateUsername("John.Doe", existingUsernames);

        assertEquals("John.Doe3", generatedUsername);
    }

    @Test
    public void testGenerateUsernameWithNoExistingIndexes() {
        List<String> existingUsernames = List.of("John.Doe");

        String generatedUsername = nameGenerator.generateUsername("John.Doe", existingUsernames);

        assertEquals("John.Doe1", generatedUsername);
    }
}
package epam.task.gymbootdb.utils.impl;

import epam.task.gymbootdb.utils.PasswordGenerator;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordGeneratorImplTest {

    private final PasswordGenerator passwordGenerator = new PasswordGeneratorImpl(10);

    @Test
    void generatePasswordCorrectLength() {
        String password = passwordGenerator.generatePassword();
        assertEquals(10, password.length());
    }

    @Test
    void generatePasswordContainsRequiredCharacters() {
        String password = passwordGenerator.generatePassword();

        assertTrue(password.chars().anyMatch(Character::isUpperCase),
                "Password should contain at least one uppercase letter");
        assertTrue(password.chars().anyMatch(Character::isLowerCase),
                "Password should contain at least one lowercase letter");
        assertTrue(password.chars().anyMatch(Character::isDigit),
                "Password should contain at least one digit");
    }

    @Test
    void generatePasswordContainsOnlyAllowedCharacters() {
        String password = passwordGenerator.generatePassword();

        assertTrue(password.matches("[A-Za-z0-9]+"),
                "Password should only contain allowed characters (letters and digits)");
    }

    @Test
    void generateUniquePasswords() {
        int numberOfPasswordsToGenerate = 1000;
        Set<String> generatedPasswords = new HashSet<>();

        for (int i = 0; i < numberOfPasswordsToGenerate; i++) {
            String password = passwordGenerator.generatePassword();
            assertTrue(generatedPasswords.add(password), "Generated password is not unique: " + password);
        }
    }
}

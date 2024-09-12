package epam.task.gymbootdb.utils.impl;

import epam.task.gymbootdb.utils.PasswordGenerator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordGeneratorImplTest {

    private final PasswordGenerator passwordGenerator = new PasswordGeneratorImpl(10);

    @Test
    public void testGeneratePasswordCorrectLength() {
        String password = passwordGenerator.generatePassword();
        assertEquals(10, password.length());
    }

    @Test
    public void testGeneratePasswordContainsRequiredCharacters() {
        String password = passwordGenerator.generatePassword();

        assertTrue(password.chars().anyMatch(Character::isUpperCase),
                "Password should contain at least one uppercase letter");
        assertTrue(password.chars().anyMatch(Character::isLowerCase),
                "Password should contain at least one lowercase letter");
        assertTrue(password.chars().anyMatch(Character::isDigit),
                "Password should contain at least one digit");
    }

    @Test
    public void testGeneratePasswordContainsOnlyAllowedCharacters() {
        String password = passwordGenerator.generatePassword();

        assertTrue(password.matches("[A-Za-z0-9]+"),
                "Password should only contain allowed characters (letters and digits)");
    }
}
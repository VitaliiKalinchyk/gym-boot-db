package epam.task.gymbootdb.utils.impl;

import epam.task.gymbootdb.utils.PasswordGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class PasswordGeneratorImpl implements PasswordGenerator {

    private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String ALL_CHARACTERS = UPPER_CASE + LOWER_CASE + DIGITS;

    @Value("${password.length}")
    private int passwordLength;

    private static final SecureRandom random = new SecureRandom();

    @Override
    public String generatePassword() {
        List<Character> passwordChars = new ArrayList<>();

        passwordChars.add(UPPER_CASE.charAt(random.nextInt(UPPER_CASE.length())));
        passwordChars.add(LOWER_CASE.charAt(random.nextInt(LOWER_CASE.length())));
        passwordChars.add(DIGITS.charAt(random.nextInt(DIGITS.length())));

        IntStream.range(passwordChars.size(), passwordLength)
                 .mapToObj(i -> ALL_CHARACTERS.charAt(random.nextInt(ALL_CHARACTERS.length())))
                 .forEach(passwordChars::add);

        Collections.shuffle(passwordChars);

        return passwordChars.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
    }
}
package epam.task.gymbootdb.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;

@Configuration
public class PasswordEncoderConfig {

    @Value("${security.bcrypt.strength}")
    private int strength;

    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder(strength, new SecureRandom());
    }
}

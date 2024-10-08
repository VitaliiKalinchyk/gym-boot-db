package epam.task.gymbootdb.config;

import epam.task.gymbootdb.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminDataInitializer implements CommandLineRunner {

    @Value("${security.admin.name}")
    private String adminName;
    @Value("${security.admin.password}")
    private String adminPassword;

    private final UserService userService;

    @Override
    public void run(String... args) {
        userService.createAdmin(adminName, adminPassword);
    }
}

package epam.task.gymbootdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GymBootDbApplication {

    public static void main(String[] args) {
        SpringApplication.run(GymBootDbApplication.class, args);
    }
}

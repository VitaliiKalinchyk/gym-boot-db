package epam.task.gymbootdb.repository;

import epam.task.gymbootdb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
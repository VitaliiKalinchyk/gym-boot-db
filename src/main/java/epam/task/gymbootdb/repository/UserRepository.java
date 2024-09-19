package epam.task.gymbootdb.repository;

import epam.task.gymbootdb.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    @Query("SELECT u.username FROM User u WHERE u.username LIKE :username%")
    List<String> findUsernamesByUsernameStartsWith(String username);
}
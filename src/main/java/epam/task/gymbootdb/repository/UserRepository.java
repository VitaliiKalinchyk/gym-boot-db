package epam.task.gymbootdb.repository;

import epam.task.gymbootdb.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    @Query("SELECT u.username FROM User u WHERE u.username LIKE :username%")
    List<String> findUsernamesByUsernameStartsWith(@Param("username") String username);
}
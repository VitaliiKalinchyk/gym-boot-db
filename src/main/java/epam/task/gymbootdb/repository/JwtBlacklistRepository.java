package epam.task.gymbootdb.repository;

import epam.task.gymbootdb.entity.JwtBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface JwtBlacklistRepository extends JpaRepository<JwtBlacklist, String> {
    void deleteAllByExpirationTimeBefore(Date expirationTime);
}

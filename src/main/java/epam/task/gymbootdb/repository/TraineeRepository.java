package epam.task.gymbootdb.repository;

import epam.task.gymbootdb.entity.Trainee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {
    Optional<Trainee> findByUserUsername(String username);

    boolean existsByUserUsername(String username);
}

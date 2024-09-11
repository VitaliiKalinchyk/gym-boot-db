package epam.task.gymbootdb.repository;

import epam.task.gymbootdb.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainingRepository extends JpaRepository<Training, Long> {
    Optional<Training> findByName(String name);
}
package epam.task.gymbootdb.repository;

import epam.task.gymbootdb.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingRepository extends JpaRepository<Training, Long> {
}
package epam.task.gymbootdb.repository;

import epam.task.gymbootdb.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {
}
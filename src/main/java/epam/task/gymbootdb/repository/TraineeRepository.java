package epam.task.gymbootdb.repository;

import epam.task.gymbootdb.entity.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {
}
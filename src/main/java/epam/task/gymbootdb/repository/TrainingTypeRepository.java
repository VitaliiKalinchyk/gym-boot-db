package epam.task.gymbootdb.repository;

import epam.task.gymbootdb.entity.TrainingType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {
}
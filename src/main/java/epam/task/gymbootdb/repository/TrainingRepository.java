package epam.task.gymbootdb.repository;

import epam.task.gymbootdb.entity.Training;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Long> {

    @Query("""
            SELECT t FROM Training t WHERE t.trainee.user.username = :username AND
            (:fromDate IS NULL OR t.date >= :fromDate) AND
            (:toDate IS NULL OR t.date <= :toDate) AND
            (:trainerId IS NULL OR t.trainer.id = :trainerId) AND
            (:trainingTypeId IS NULL OR t.trainingType.id = :trainingTypeId)""")
    List<Training> findTraineeTrainingsByOptionalParams(
            String username,
            LocalDate fromDate,
            LocalDate toDate,
            Long trainerId,
            Long trainingTypeId);

    @Query("""
            SELECT t FROM Training t WHERE t.trainer.user.username = :username AND
            (:fromDate IS NULL OR t.date >= :fromDate) AND
            (:toDate IS NULL OR t.date <= :toDate) AND
            (:traineeId IS NULL OR t.trainee.id = :traineeId)""")
    List<Training> findTrainerTrainingsByOptionalParams(
            String username,
            LocalDate fromDate,
            LocalDate toDate,
            Long traineeId);
}

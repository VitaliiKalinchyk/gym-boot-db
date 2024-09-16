package epam.task.gymbootdb.repository;

import epam.task.gymbootdb.entity.Training;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Long> {

    @Query("SELECT t FROM Training t WHERE t.trainee.user.username = :traineeUsername AND " +
            "(:fromDate IS NULL OR t.date >= :fromDate) AND " +
            "(:toDate IS NULL OR t.date <= :toDate) AND " +
            "(:trainerUsername IS NULL OR t.trainer.user.username = :trainerUsername) AND " +
            "(:trainingTypeName IS NULL OR t.trainingType.name = :trainingTypeName)")
    List<Training> findTraineeTrainingsByOptionalParams(
            String traineeUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerUsername,
            String trainingTypeName);

    @Query("SELECT t FROM Training t WHERE t.trainer.user.username = :trainerUsername AND " +
            "(:fromDate IS NULL OR t.date >= :fromDate) AND " +
            "(:toDate IS NULL OR t.date <= :toDate) AND " +
            "(:traineeUsername IS NULL OR t.trainee.user.username = :traineeUsername)")
    List<Training> findTrainerTrainingsByOptionalParams(
            String trainerUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeUsername);
}
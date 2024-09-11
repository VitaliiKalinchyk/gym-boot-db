package epam.task.gymbootdb.repository;

import epam.task.gymbootdb.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingRepository extends JpaRepository<Training, Long> {

    Optional<Training> findByName(String name);

    @Query("SELECT t FROM Training t WHERE t.trainee.user.username = :traineeUsername AND " +
            "(:fromDate IS NULL OR t.date >= :fromDate) AND " +
            "(:toDate IS NULL OR t.date <= :toDate) AND " +
            "(:trainerUsername IS NULL OR t.trainer.user.username = :trainerUsername) AND " +
            "(:trainingTypeName IS NULL OR t.trainingType.name = :trainingTypeName)")
    List<Training> findTrainingsByOptionalParams(
            @Param("traineeUsername") String traineeUsername,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("trainerUsername") String trainerUsername,
            @Param("trainingTypeName") String trainingTypeName);

    @Query("SELECT t FROM Training t WHERE t.trainer.user.username = :trainerUsername AND " +
            "(:fromDate IS NULL OR t.date >= :fromDate) AND " +
            "(:toDate IS NULL OR t.date <= :toDate) AND " +
            "(:traineeUsername IS NULL OR t.trainee.user.username = :traineeUsername)")
    List<Training> findTrainingsByOptionalParams(
            @Param("trainerUsername") String trainerUsername,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("traineeUsername") String traineeUsername);
}
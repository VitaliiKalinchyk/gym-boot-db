package epam.task.gymbootdb.repository;

import epam.task.gymbootdb.entity.Trainer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    @Query("SELECT t FROM Trainer t WHERE t.id NOT IN " +
            "(SELECT tt.id FROM Trainee tr JOIN tr.trainers tt WHERE tr.id = :id)")
    List<Trainer> findTrainersNotAssignedToTrainee(long id);
}
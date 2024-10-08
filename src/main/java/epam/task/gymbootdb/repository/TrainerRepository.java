package epam.task.gymbootdb.repository;

import epam.task.gymbootdb.entity.Trainer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    boolean existsByUserUsername(String username);

    Optional<Trainer> findByUserUsername(String username);

    @Query("""
        SELECT t FROM Trainer t WHERE t.id NOT IN
        (SELECT tt.id FROM Trainee tr JOIN tr.trainers tt JOIN tr.user u WHERE u.username = :username)""")
    List<Trainer> findTrainersNotAssignedToTrainee(String username);
}

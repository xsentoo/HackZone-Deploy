
package com.uphf.HackZone.Repository;

import com.uphf.HackZone.Entity.SolveEntity; // Au singulier
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SolveRepository extends JpaRepository<SolveEntity,Integer> {

    // Méthode utilisée par GamificationController pour empêcher la double soumission
    boolean existsByUserIdAndAttId(int userId,int attId);

    // Méthode CRITIQUE utilisée par LearningController pour vérifier le statut d'affichage
    Optional<SolveEntity> findByUserIdAndAttId(int userId, int attId);
}
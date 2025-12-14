package com.uphf.HackZone.Service;
import com.uphf.HackZone.Entity.AttackEntity;
import com.uphf.HackZone.Entity.SolveEntity;
import com.uphf.HackZone.Entity.UserEntity;
import com.uphf.HackZone.Repository.AttackRepository;
import com.uphf.HackZone.Repository.SolveRepository;
import com.uphf.HackZone.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ChallengeService {

    @Autowired
    private AttackRepository attackRepository;

    @Autowired
    private SolveRepository solveRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Récupérer tous les challenges
     */
    public List<AttackEntity> getAllChallenges() {
        return attackRepository.findAll();
    }

    /**
     * Récupérer un challenge par ID
     */
    public Optional<AttackEntity> getChallengeById(int id) {
        return attackRepository.findById(id);
    }

    /**
     * Récupérer les challenges par catégorie
     */
    public List<AttackEntity> getChallengesByCategory(String category) {
        // Optimisation : si AttackRepository a findByCategory, il est préférable
        // de l'utiliser plutôt que de filtrer manuellement.
        return attackRepository.findByCategory(category);
    }

    /**
     * Récupérer les challenges par difficulté
     */
    public List<AttackEntity> getChallengesByDifficulty(String difficulty) {
        return attackRepository.findAll().stream()
                .filter(attack -> attack.getDifficulty().equalsIgnoreCase(difficulty))
                .toList();
    }

    /**
     * Vérifier si un utilisateur a déjà résolu un challenge
     */
    public boolean isChallengeSolved(int userId, int attId) {
        // CORRECTION/OPTIMISATION : Utilise la méthode native existsByUserIdAndAttId du Repository
        return solveRepository.existsByUserIdAndAttId(userId, attId);
    }

    /**
     * Valider un flag et attribuer les points
     */
    @Transactional
    public Map<String, Object> submitFlag(int challengeId, int userId, String submittedFlag) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Récupérer le challenge
            Optional<AttackEntity> attackOpt = attackRepository.findById(challengeId);
            if (!attackOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Challenge introuvable");
                return response;
            }

            AttackEntity attack = attackOpt.get();

            // Récupérer l'utilisateur
            Optional<UserEntity> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Utilisateur introuvable");
                return response;
            }

            UserEntity user = userOpt.get();

            // Vérifier si déjà résolu
            if (isChallengeSolved(userId, challengeId)) {
                response.put("success", false);
                response.put("message", "Vous avez déjà validé ce challenge !");
                return response;
            }

            // Vérifier le flag
            if (attack.getFlag().equals(submittedFlag)) {
                // Flag correct !

                // Enregistrer dans Solves
                SolveEntity solve = new SolveEntity(userId, challengeId);
                solveRepository.save(solve);

                // Ajouter les points
                user.setPoint(user.getPoint() + attack.getPoints());
                // Sauvegarder l'utilisateur (CRITIQUE : Cette ligne garantit la mise à jour des points)
                userRepository.save(user);

                response.put("success", true);
                response.put("message", "🎉 Bravo ! Flag correct !");
                response.put("points", attack.getPoints());
                response.put("totalPoints", user.getPoint());

                return response;
            } else {
                // Flag incorrect
                response.put("success", false);
                response.put("message", "❌ Flag incorrect, réessayez !");
                return response;
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return response;
        }
    }

    /**
     * Récupérer les challenges résolus par un utilisateur
     */
    public List<SolveEntity> getSolvedChallenges(int userId) {
        return solveRepository.findAll().stream()
                .filter(solve -> solve.getUserId() == userId)
                .toList();
    }

    /**
     * Compter le nombre de challenges résolus par un utilisateur
     */
    public long countSolvedChallenges(int userId) {
        return solveRepository.findAll().stream()
                .filter(solve -> solve.getUserId() == userId)
                .count();
    }
}
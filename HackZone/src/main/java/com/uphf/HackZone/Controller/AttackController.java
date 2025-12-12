package com.uphf.HackZone.Controller;

import com.uphf.HackZone.Entity.AttackEntity;
import com.uphf.HackZone.Entity.SolveEntity;
import com.uphf.HackZone.Service.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/attacks")
@CrossOrigin(origins = "*")
public class AttackController {

    @Autowired
    private ChallengeService challengeService;

    /**
     * GET /api/attacks
     * Récupérer tous les challenges
     */
    @GetMapping
    public ResponseEntity<List<AttackEntity>> getAllAttacks() {
        return ResponseEntity.ok(challengeService.getAllChallenges());
    }

    /**
     * GET /api/attacks/{id}
     * Récupérer un challenge par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<AttackEntity> getAttackById(@PathVariable int id) {
        Optional<AttackEntity> attack = challengeService.getChallengeById(id);
        return attack.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * GET /api/attacks/category/{category}
     * Récupérer les challenges par catégorie
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<AttackEntity>> getAttacksByCategory(@PathVariable String category) {
        return ResponseEntity.ok(challengeService.getChallengesByCategory(category));
    }

    /**
     * GET /api/attacks/difficulty/{difficulty}
     * Récupérer les challenges par difficulté
     */
    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<AttackEntity>> getAttacksByDifficulty(@PathVariable String difficulty) {
        return ResponseEntity.ok(challengeService.getChallengesByDifficulty(difficulty));
    }

    /**
     * POST /api/attacks/{id}/submit
     * Soumettre un flag
     */
    @PostMapping("/{id}/submit")
    public ResponseEntity<Map<String, Object>> submitFlag(
            @PathVariable int id,
            @RequestBody Map<String, Object> payload) {
        try {
            int userId = Integer.parseInt(payload.get("userId").toString());
            String flag = payload.get("flag").toString();
            Map<String, Object> response = challengeService.submitFlag(id, userId, flag);
            if ((boolean) response.get("success")) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Erreur lors de la soumission du flag : " + e.getMessage()));
        }
    }

    /**
     * GET /api/attacks/user/{userId}/solved
     * Récupérer les challenges résolus par un utilisateur
     */
    @GetMapping("/user/{userId}/solved")
    public ResponseEntity<List<SolveEntity>> getSolvedAttacks(@PathVariable int userId) {
        return ResponseEntity.ok(challengeService.getSolvedChallenges(userId));
    }
}

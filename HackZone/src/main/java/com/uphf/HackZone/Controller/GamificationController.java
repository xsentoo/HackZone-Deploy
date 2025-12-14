package com.uphf.HackZone.Controller;

import com.uphf.HackZone.Entity.AttackEntity;
import com.uphf.HackZone.Entity.SolveEntity;
import com.uphf.HackZone.Entity.UserEntity;
import com.uphf.HackZone.Repository.AttackRepository;
import com.uphf.HackZone.Repository.SolveRepository;
import com.uphf.HackZone.Repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody; // INDISPENSABLE

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class GamificationController {
    private final AttackRepository attackRepository;
    private final SolveRepository solveRepository;
    private final UserRepository userRepository;

    public GamificationController(AttackRepository attackRepository, UserRepository userRepository, SolveRepository solveRepository) {
        this.attackRepository = attackRepository;
        this.userRepository = userRepository;
        this.solveRepository = solveRepository;
    }

    @PostMapping("/validate-flag")
    @ResponseBody // Renvoie du JSON au lieu d'une redirection HTML
    public Map<String, Object> validateFlag(@RequestParam String flagInput, @RequestParam int attId) {

        Map<String, Object> response = new HashMap<>();

        String userMail = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOpt = userRepository.findByUserMail(userMail);

        if (userOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Erreur : Utilisateur non connecté.");
            return response;
        }
        UserEntity user = userOpt.get();

        Optional<AttackEntity> attackOpt = attackRepository.findByFlag(flagInput);

        if (attackOpt.isPresent()) {
            AttackEntity attack = attackOpt.get();

            // Vérifie si le flag correspond bien au challenge actuel
            if (attack.getAttId() != attId) {
                response.put("success", false);
                response.put("message", "Ce flag est valide... mais pour un autre challenge ! Bien essayé.");
                return response;
            }

            // Vérifie si déjà résolu
            if (solveRepository.existsByUserIdAndAttId(user.getUserId(), attack.getAttId())) {
                response.put("success", false);
                response.put("message", "Déjà validé ! Petit malin...");
                return response;
            }

            // Sauvegarde la résolution
            SolveEntity solveEntity = new SolveEntity(user.getUserId(), attack.getAttId());
            solveRepository.save(solveEntity);

            // Ajoute les points
            int nouveauxPoints = user.getPoint() + attack.getPoints();
            user.setPoint(nouveauxPoints);
            updateLevel(user);
            userRepository.save(user);

            response.put("success", true);
            response.put("message", "MISSION ACCOMPLIE ! Flag correct : + " + attack.getPoints() + " XP");
            return response;

        } else {
            response.put("success", false);
            response.put("message", "ACCÈS REFUSÉ : Flag incorrect.");
            return response;
        }
    }

    private void updateLevel(UserEntity user) {
        int p = user.getPoint();
        if (p >= 1500) {
            user.setLevel("avan");
            user.setUserBadge("Master Hacker");
        } else if (p >= 500) {
            user.setLevel("int");
            user.setUserBadge("Script Kiddie");
        } else {
            user.setLevel("deb");
            user.setUserBadge("Novice");
        }
    }
}
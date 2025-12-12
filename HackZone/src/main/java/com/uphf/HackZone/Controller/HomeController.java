package com.uphf.HackZone.Controller;

import com.uphf.HackZone.Entity.UserEntity;
import com.uphf.HackZone.Repository.UserRepository;
import com.uphf.HackZone.Service.ChallengeService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    private final UserRepository userRepository;
    private final ChallengeService challengeService; // Déclaration du service

    // Constructeur mis à jour pour inclure ChallengeService
    public HomeController(UserRepository userRepository, ChallengeService challengeService) {
        this.userRepository = userRepository;
        this.challengeService = challengeService; // Initialisation
    }

    @GetMapping("/Home")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userMail = auth.getName();
        Optional<UserEntity> userOpt = userRepository.findByUserMail(userMail);

        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();


            boolean hasChanged = checkLevelConsistency(user);
            if (hasChanged) {
                userRepository.save(user); // On sauvegarde le nouveau niveau en BDD
            }
            // --------------------------------------------------

            model.addAttribute("user", user);

            // AJOUT : Récupérer et envoyer tous les challenges à la vue
            model.addAttribute("allAttacks", challengeService.getAllChallenges());

            // Calcul pour la barre de progression
            int score = user.getPoint();
            int nextLevelScore = 500;

            if ("int".equals(user.getLevel())) {
                nextLevelScore = 1500;
            } else if ("avan".equals(user.getLevel())) {
                nextLevelScore = 5000;
            }

            int percent = 0;
            if (nextLevelScore > 0) {
                percent = (int) ((score * 100.0) / nextLevelScore);
            }
            if (percent > 100) percent = 100;

            model.addAttribute("nextLevelScore", nextLevelScore);
            model.addAttribute("progressPercent", percent);

            // Gestion du classement
            List<UserEntity> leaderboard = userRepository.findTop10ByOrderByPointDesc();
            if (leaderboard.size() > 5) {
                leaderboard = leaderboard.subList(0, 5);
            }
            model.addAttribute("leaderboard", leaderboard);

            return "Home";
        }
        return "redirect:/Auth/login";
    }

    // --- Méthode utilitaire pour vérifier le niveau ---
    private boolean checkLevelConsistency(UserEntity user) {
        int p = user.getPoint();
        String currentLevel = user.getLevel();
        String newLevel = currentLevel;
        String newBadge = user.getUserBadge();

        if (p >= 1500) {
            newLevel = "avan";
            newBadge = "Master Hacker";
        } else if (p >= 500) {
            newLevel = "int";
            newBadge = "Script Kiddie";
        } else {
            newLevel = "deb";
            newBadge = "Novice";
        }


        if (!newLevel.equals(currentLevel)) {
            user.setLevel(newLevel);
            user.setUserBadge(newBadge);
            return true;
        }
        return false; // Pas de changement
    }
    @GetMapping("/Leaderboard")
    public String showLeaderboardPage(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userMail = auth.getName();
        userRepository.findByUserMail(userMail).ifPresent(u -> model.addAttribute("currentUser", u));


        List<UserEntity> allPlayers = userRepository.findAll(
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "point")
        );

        model.addAttribute("leaderboard", allPlayers);

        return "Leaderboard"; // Cela va ouvrir src/main/resources/templates/Leaderboard.html
    }
}
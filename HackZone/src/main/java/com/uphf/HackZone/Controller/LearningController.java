package com.uphf.HackZone.Controller;

import com.uphf.HackZone.Entity.AttackEntity;
import com.uphf.HackZone.Repository.AttackRepository;
import com.uphf.HackZone.Repository.UserRepository;
import com.uphf.HackZone.Entity.UserEntity;
import com.uphf.HackZone.Entity.SolveEntity; // Au singulier
import com.uphf.HackZone.Repository.SolveRepository; // Au singulier
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Controller
public class LearningController {

    private final AttackRepository attackRepository;
    private final UserRepository userRepository;
    private final SolveRepository solveRepository; // Nom de la propriété corrigé

    public LearningController(AttackRepository attackRepository, UserRepository userRepository, SolveRepository solveRepository) {
        this.attackRepository = attackRepository;
        this.userRepository = userRepository;
        this.solveRepository = solveRepository;
    }

    @GetMapping("/learn/{category}")
    public String showCategoryLevels(@PathVariable String category, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userMail = auth.getName();

        userRepository.findByUserMail(userMail).ifPresent(u -> model.addAttribute("user", u));

        List<AttackEntity> challenges = attackRepository.findByCategory(category);

        model.addAttribute("categoryName", category);
        model.addAttribute("challenges", challenges);

        return "Learning/LevelSelect";
    }

    // METHODE CRITIQUE : AFFICHE LES DÉTAILS DE LA MISSION (/mission/{id})
    @GetMapping("/mission/{id}")
    public String showMissionPage(@PathVariable int id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userMail = auth.getName();

        Optional<UserEntity> userOpt = userRepository.findByUserMail(userMail);

        if (userOpt.isEmpty()) {
            return "redirect:/Auth/login";
        }

        UserEntity user = userOpt.get();
        model.addAttribute("user", user);


        Optional<AttackEntity> attackOpt = attackRepository.findById(id);

        if (attackOpt.isPresent()) {
            AttackEntity challenge = attackOpt.get();
            model.addAttribute("challenge", challenge);

            // VÉRIFICATION DE LA SOLUTION : Utilise le Repository correct
            Optional<SolveEntity> solveOpt = solveRepository.findByUserIdAndAttId(user.getUserId(), id);
            boolean isSolved = solveOpt.isPresent(); // La méthode isPresent est sûre.

            model.addAttribute("userSolved", isSolved);

            return "Learning/MissionRoom";
        }

        // Si l'ID de la mission n'est pas trouvé
        return "redirect:/Home";
    }
}
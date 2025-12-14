package com.uphf.HackZone.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="UserHack")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId") // AJOUTÉ : Correspond à ta capture d'écran
    private int userId;

    @Column(name = "userName") // AJOUTÉ : Indispensable pour éviter "user_name"
    private String userName;

    @Column(name = "userMail") // AJOUTÉ
    private String userMail;

    @Column(name = "userPWD") // AJOUTÉ
    private String userPWD;

    @Column(name = "level") // Optionnel car pas de majuscule, mais plus sûr
    private String level;
    @Column(name = "userBadge") // AJOUTÉ
    private String userBadge;

    @Column(name="userDate") // Tu l'avais déjà, c'est bien
    private LocalDate userDate;

    @Column(name = "point") // Tu l'avais déjà, c'est bien
    private int point = 0;

    public UserEntity() {
    }

    public UserEntity(int userId, String userName, String userMail, String userPWD, String level, String userBadge, LocalDate userDate, int point) {
        this.userId = userId;
        this.userName = userName;
        this.userMail = userMail;
        this.userPWD = userPWD;
        this.level = level;
        this.userBadge = userBadge;
        this.userDate = userDate;
        this.point = point;
    }

    // --- GETTERS / SETTERS (Inchangés) ---
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserMail() { return userMail; }
    public void setUserMail(String userMail) { this.userMail = userMail; }

    public String getUserPWD() { return userPWD; }
    public void setUserPWD(String userPWD) { this.userPWD = userPWD; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getUserBadge() { return userBadge; }
    public void setUserBadge(String userBadge) { this.userBadge = userBadge; }

    public LocalDate getUserDate() { return userDate; }
    public void setUserDate(LocalDate userDate) { this.userDate = userDate; }

    public int getPoint() { return point; }
    public void setPoint(int point) { this.point = point; }
}
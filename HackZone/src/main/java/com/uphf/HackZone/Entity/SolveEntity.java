package com.uphf.HackZone.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Solves")
public class SolveEntity { // <-- CORRIGÉ : Le nom de la classe est maintenant 'SolveEntity'
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int solveid;
    private int userId;
    private int attId;
    private LocalDateTime solved_at;

    public SolveEntity(int userId , int attId) {
        this.userId = userId;
        this.attId = attId;
        this.solved_at = LocalDateTime.now();
    }
    // ... (Le reste des méthodes Getters/Setters reste inchangé) ...

    public SolveEntity() {
    }

    public int getSolveid() {
        return solveid;
    }

    public void setSolveid(int solveid) {
        this.solveid = solveid;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAttId() {
        return attId;
    }

    public void setAttId(int attId) {
        this.attId = attId;
    }

    public LocalDateTime getSolved_at() {
        return solved_at;
    }

    public void setSolved_at(LocalDateTime solved_at) {
        this.solved_at = solved_at;
    }
}
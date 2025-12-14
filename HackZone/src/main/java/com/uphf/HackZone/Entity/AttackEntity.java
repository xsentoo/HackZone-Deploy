package com.uphf.HackZone.Entity;

import jakarta.persistence.*;

@Entity
@Table(name="Attacks")
public class AttackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int attId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category;
    private String difficulty;

    @Column(name = "target_url")
    private String targetUrl; // Nom Java correct

    private String flag;
    private int points;

    public AttackEntity() {
    }

    public int getAttId() {
        return attId;
    }

    public void setAttId(int attId) {
        this.attId = attId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
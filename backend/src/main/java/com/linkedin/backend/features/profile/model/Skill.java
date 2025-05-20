package com.linkedin.backend.features.profile.model;

import com.linkedin.backend.features.authentication.model.User;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "skills")
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String name;
    private int level;

    // Compteur pour le nombre d'endorsements
    private int endorsementCount = 0;

    @ManyToMany
    @JoinTable(
            name = "skill_endorsements",
            joinColumns = @JoinColumn(name = "skill_id"),
            inverseJoinColumns = @JoinColumn(name = "endorser_id")
    )
    private Set<User> endorsements = new HashSet<>();

    // Default constructor
    public Skill() {}

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    // Méthodes pour gérer le compteur d'endorsements
    public int getEndorsements() {
        return endorsementCount;
    }

    public void setEndorsements(int count) {
        this.endorsementCount = count;
    }

    // Méthodes pour gérer les utilisateurs qui ont endossé
    public Set<User> getEndorsementUsers() {
        return endorsements;
    }

    public void setEndorsementUsers(Set<User> endorsements) {
        this.endorsements = endorsements;
    }

    public void addEndorsement(User endorser) {
        this.endorsements.add(endorser);
        this.endorsementCount++;
    }

    public void removeEndorsement(User endorser) {
        if (this.endorsements.remove(endorser)) {
            this.endorsementCount--;
        }
    }

    public int getEndorsementCount() {
        return this.endorsementCount;
    }
}
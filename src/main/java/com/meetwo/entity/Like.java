package com.meetwo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "likes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"liker_id", "liked_user_id"})
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_like")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liker_id", nullable = false)
    private User liker; // L'utilisateur qui like

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liked_user_id", nullable = false)
    private User likedUser; // L'utilisateur qui est liké

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructeur utilitaire
    public Like(User liker, User likedUser) {
        this.liker = liker;
        this.likedUser = likedUser;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Méthode utilitaire pour vérifier si c'est un match mutuel
    public boolean isMatch(Like otherLike) {
        return this.liker.getId().equals(otherLike.getLikedUser().getId()) &&
                this.likedUser.getId().equals(otherLike.getLiker().getId());
    }
}
package com.meetwo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "photos",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "position"})
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_photo")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(nullable = false)
    private Integer position;

    @Column(name = "est_principale", nullable = false)
    private Boolean estPrincipale = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Métadonnées optionnelles
    @Column(name = "alt_text", length = 255)
    private String altText;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "content_type", length = 50)
    private String contentType;

    // Constructeur utilitaire
    public Photo(User user, String url, Integer position, Boolean estPrincipale) {
        this.user = user;
        this.url = url;
        this.position = position;
        this.estPrincipale = estPrincipale != null ? estPrincipale : false;
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
        if (estPrincipale == null) {
            estPrincipale = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
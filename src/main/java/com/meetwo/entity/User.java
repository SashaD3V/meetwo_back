package com.meetwo.entity;

import com.meetwo.enums.Gender;
import com.meetwo.enums.Interest;
import com.meetwo.enums.RelationshipType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // AJOUT DU CHAMP username (requis par UserDetails)
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    // CHAMP name pour l'affichage
    @Column(nullable = false, length = 200)
    private String name;

    // Informations personnelles
    @Column(length = 100)
    private String firstName;

    @Column(length = 100)
    private String lastName;

    @Column
    private LocalDate birthDate;

    // AJOUT DU CHAMP AGE
    @Column(nullable = false)
    private Integer age = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(length = 500)
    private String biography;

    @Column(length = 100)
    private String city;

    @ElementCollection(targetClass = Interest.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_interests", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "interest")
    private Set<Interest> interests = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RelationshipType seekingRelationshipType;

    // Métadonnées
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private boolean accountNonExpired = true;

    @Column(nullable = false)
    private boolean accountNonLocked = true;

    @Column(nullable = false)
    private boolean credentialsNonExpired = true;

    // Constructeur personnalisé
    public User(String username, String email, String password, Gender gender, RelationshipType seekingRelationshipType) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.seekingRelationshipType = seekingRelationshipType;
        this.name = username; // Valeur par défaut
        this.age = 0; // Valeur par défaut
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.interests = new HashSet<>();
    }

    // Méthodes UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    // Méthode pour calculer l'âge
    public Integer getAge() {
        return age;
    }

    // Méthode privée pour calculer l'âge basé sur birthDate
    private int calculateAge() {
        if (birthDate == null) {
            return 0;
        }
        return LocalDate.now().getYear() - birthDate.getYear();
    }

    // Génération automatique du name
    public void generateName() {
        if (firstName != null && lastName != null) {
            this.name = firstName + " " + lastName;
        } else if (firstName != null) {
            this.name = firstName;
        } else if (lastName != null) {
            this.name = lastName;
        } else {
            this.name = username;
        }
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (name == null || name.trim().isEmpty()) {
            generateName();
        }
        // Calculer l'âge avant la sauvegarde
        this.age = calculateAge();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        generateName();
        // Recalculer l'âge à chaque mise à jour
        this.age = calculateAge();
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("position ASC")
    private List<Photo> photos = new ArrayList<>();

    // Méthodes utilitaires à ajouter dans User :
    public Photo getMainPhoto() {
        return photos.stream()
                .filter(Photo::getEstPrincipale)
                .findFirst()
                .orElse(null);
    }

    public List<Photo> getPhotosOrderedByPosition() {
        return photos.stream()
                .sorted(Comparator.comparing(Photo::getPosition))
                .collect(Collectors.toList());
    }

    public void setMainPhoto(Photo photo) {
        // Retirer le statut principal des autres photos
        photos.forEach(p -> p.setEstPrincipale(false));
        // Définir la nouvelle photo principale
        photo.setEstPrincipale(true);
    }
}
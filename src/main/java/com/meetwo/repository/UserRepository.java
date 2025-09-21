package com.meetwo.repository;

import com.meetwo.entity.User;
import com.meetwo.enums.Gender;
import com.meetwo.enums.Interest;
import com.meetwo.enums.RelationshipType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Méthodes de base pour l'authentification
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // Méthodes pour le site de rencontre - recherche de profils
    List<User> findByGenderAndEnabledTrue(Gender gender);

    List<User> findBySeekingRelationshipTypeAndEnabledTrue(RelationshipType relationshipType);

    List<User> findByCityIgnoreCaseAndEnabledTrue(String city);

    List<User> findByBirthDateBetweenAndEnabledTrue(LocalDate startDate, LocalDate endDate);

    // Recherche par tranche d'âge (méthode personnalisée)
    @Query("SELECT u FROM User u WHERE u.enabled = true AND " +
            "YEAR(CURRENT_DATE) - YEAR(u.birthDate) BETWEEN :minAge AND :maxAge")
    List<User> findByAgeBetween(@Param("minAge") int minAge, @Param("maxAge") int maxAge);

    // Recherche par intérêts communs
    @Query("SELECT u FROM User u JOIN u.interests i WHERE i IN :interests AND u.enabled = true")
    List<User> findByInterestsIn(@Param("interests") Set<Interest> interests);

    // Recherche avancée pour le matching
    @Query("SELECT u FROM User u WHERE u.enabled = true AND " +
            "u.gender = :gender AND " +
            "u.seekingRelationshipType = :relationshipType AND " +
            "(:city IS NULL OR LOWER(u.city) = LOWER(:city)) AND " +
            "(:minAge IS NULL OR YEAR(CURRENT_DATE) - YEAR(u.birthDate) >= :minAge) AND " +
            "(:maxAge IS NULL OR YEAR(CURRENT_DATE) - YEAR(u.birthDate) <= :maxAge)")
    List<User> findPotentialMatches(@Param("gender") Gender gender,
                                    @Param("relationshipType") RelationshipType relationshipType,
                                    @Param("city") String city,
                                    @Param("minAge") Integer minAge,
                                    @Param("maxAge") Integer maxAge);

    // Recherche par ville avec pagination (pour de gros volumes)
    List<User> findByCityIgnoreCaseAndEnabledTrueOrderByCreatedAtDesc(String city);

    // Recherche des nouveaux utilisateurs
    @Query("SELECT u FROM User u WHERE u.enabled = true ORDER BY u.createdAt DESC")
    List<User> findNewestUsers();

    // Recherche d'utilisateurs avec au moins une biographie
    List<User> findByBiographyIsNotNullAndEnabledTrueOrderByUpdatedAtDesc();

    // Compter les utilisateurs par genre
    long countByGenderAndEnabledTrue(Gender gender);

    // Compter les utilisateurs par ville
    long countByCityIgnoreCaseAndEnabledTrue(String city);

    // Recherche d'utilisateurs exclus (pour éviter de les montrer à nouveau)
    @Query("SELECT u FROM User u WHERE u.enabled = true AND u.id NOT IN :excludedIds")
    List<User> findUsersExcluding(@Param("excludedIds") List<Long> excludedIds);

    // Recherche d'utilisateurs avec des intérêts similaires mais pas exactement les mêmes
    @Query("SELECT DISTINCT u FROM User u JOIN u.interests ui WHERE " +
            "EXISTS (SELECT 1 FROM User u2 JOIN u2.interests u2i WHERE u2.id = :userId AND ui = u2i) AND " +
            "u.id != :userId AND u.enabled = true")
    List<User> findUsersWithSimilarInterests(@Param("userId") Long userId);
}
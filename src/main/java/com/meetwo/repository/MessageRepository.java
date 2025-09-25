package com.meetwo.repository;

import com.meetwo.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Récupérer tous les messages entre deux utilisateurs
    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.id = :userId1 AND m.receiver.id = :userId2) OR " +
            "(m.sender.id = :userId2 AND m.receiver.id = :userId1) " +
            "ORDER BY m.createdAt ASC")
    List<Message> findConversationBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // Récupérer les messages d'une conversation avec pagination
    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.id = :userId1 AND m.receiver.id = :userId2 AND m.isDeletedBySender = false) OR " +
            "(m.sender.id = :userId2 AND m.receiver.id = :userId1 AND m.isDeletedByReceiver = false) " +
            "ORDER BY m.createdAt DESC LIMIT :limit")
    List<Message> findRecentMessagesBetweenUsers(@Param("userId1") Long userId1,
                                                 @Param("userId2") Long userId2,
                                                 @Param("limit") int limit);

    // Récupérer le dernier message entre deux utilisateurs
    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.id = :userId1 AND m.receiver.id = :userId2) OR " +
            "(m.sender.id = :userId2 AND m.receiver.id = :userId1) " +
            "ORDER BY m.createdAt DESC LIMIT 1")
    Optional<Message> findLastMessageBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // Compter les messages non lus pour un utilisateur
    @Query("SELECT COUNT(m) FROM Message m WHERE " +
            "m.receiver.id = :userId AND m.isRead = false AND m.isDeletedByReceiver = false")
    long countUnreadMessagesByReceiver(@Param("userId") Long userId);

    // Compter les messages non lus d'une conversation spécifique
    @Query("SELECT COUNT(m) FROM Message m WHERE " +
            "m.receiver.id = :receiverId AND m.sender.id = :senderId AND " +
            "m.isRead = false AND m.isDeletedByReceiver = false")
    long countUnreadMessagesInConversation(@Param("receiverId") Long receiverId, @Param("senderId") Long senderId);

    // Récupérer tous les messages envoyés par un utilisateur
    List<Message> findBySenderIdAndIsDeletedBySenderFalseOrderByCreatedAtDesc(Long senderId);

    // Récupérer tous les messages reçus par un utilisateur
    List<Message> findByReceiverIdAndIsDeletedByReceiverFalseOrderByCreatedAtDesc(Long receiverId);

    // Récupérer les messages non lus d'un utilisateur
    List<Message> findByReceiverIdAndIsReadFalseAndIsDeletedByReceiverFalseOrderByCreatedAtDesc(Long receiverId);

    // Trouver les utilisateurs avec qui un utilisateur a des conversations
    @Query("SELECT DISTINCT " +
            "CASE WHEN m.sender.id = :userId THEN m.receiver.id ELSE m.sender.id END " +
            "FROM Message m WHERE " +
            "(m.sender.id = :userId AND m.isDeletedBySender = false) OR " +
            "(m.receiver.id = :userId AND m.isDeletedByReceiver = false)")
    List<Long> findConversationPartnerIds(@Param("userId") Long userId);

    // Marquer tous les messages d'une conversation comme lus
    @Query("UPDATE Message m SET m.isRead = true, m.readAt = :readAt WHERE " +
            "m.receiver.id = :receiverId AND m.sender.id = :senderId AND m.isRead = false")
    void markConversationAsRead(@Param("receiverId") Long receiverId,
                                @Param("senderId") Long senderId,
                                @Param("readAt") LocalDateTime readAt);

    // Supprimer logiquement les messages d'un côté de la conversation
    @Query("UPDATE Message m SET m.isDeletedBySender = true WHERE " +
            "m.sender.id = :userId AND " +
            "(m.receiver.id = :otherUserId OR :otherUserId IS NULL)")
    void markMessagesAsDeletedBySender(@Param("userId") Long userId, @Param("otherUserId") Long otherUserId);

    @Query("UPDATE Message m SET m.isDeletedByReceiver = true WHERE " +
            "m.receiver.id = :userId AND " +
            "(m.sender.id = :otherUserId OR :otherUserId IS NULL)")
    void markMessagesAsDeletedByReceiver(@Param("userId") Long userId, @Param("otherUserId") Long otherUserId);

    // Supprimer physiquement les messages supprimés des deux côtés
    @Query("DELETE FROM Message m WHERE m.isDeletedBySender = true AND m.isDeletedByReceiver = true")
    void deleteMessagesMarkedForDeletion();

    // Statistiques - Compter les messages envoyés par un utilisateur
    long countBySenderIdAndIsDeletedBySenderFalse(Long senderId);

    // Statistiques - Compter les messages reçus par un utilisateur
    long countByReceiverIdAndIsDeletedByReceiverFalse(Long receiverId);

    // Récupérer les messages récents (pour notifications)
    @Query("SELECT m FROM Message m WHERE " +
            "m.receiver.id = :userId AND m.createdAt >= :since AND " +
            "m.isDeletedByReceiver = false ORDER BY m.createdAt DESC")
    List<Message> findRecentMessagesForUser(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    // Vérifier s'il existe au moins un message entre deux utilisateurs
    @Query("SELECT COUNT(m) > 0 FROM Message m WHERE " +
            "(m.sender.id = :userId1 AND m.receiver.id = :userId2) OR " +
            "(m.sender.id = :userId2 AND m.receiver.id = :userId1)")
    boolean existsConversationBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // Rechercher des messages par contenu (pour futures fonctionnalités)
    @Query("SELECT m FROM Message m WHERE " +
            "((m.sender.id = :userId AND m.isDeletedBySender = false) OR " +
            "(m.receiver.id = :userId AND m.isDeletedByReceiver = false)) AND " +
            "LOWER(m.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "ORDER BY m.createdAt DESC")
    List<Message> searchMessagesByContent(@Param("userId") Long userId, @Param("searchTerm") String searchTerm);

    // Supprimer tous les messages d'un utilisateur (suppression de compte)
    void deleteBySenderIdOrReceiverId(Long senderId, Long receiverId);

    // Compter les conversations actives d'un utilisateur
    @Query("SELECT COUNT(DISTINCT " +
            "CASE WHEN m.sender.id = :userId THEN m.receiver.id ELSE m.sender.id END) " +
            "FROM Message m WHERE " +
            "(m.sender.id = :userId AND m.isDeletedBySender = false) OR " +
            "(m.receiver.id = :userId AND m.isDeletedByReceiver = false)")
    long countActiveConversationsByUser(@Param("userId") Long userId);
}
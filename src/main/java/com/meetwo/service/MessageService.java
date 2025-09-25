package com.meetwo.service;

import com.meetwo.dto.request.CreateMessageRequest;
import com.meetwo.dto.request.UpdateMessageRequest;
import com.meetwo.dto.response.MessageResponse;
import com.meetwo.dto.response.ConversationResponse;
import com.meetwo.dto.response.MessageStatsResponse;

import java.util.List;

/**
 * Service pour la gestion des messages dans l'application de rencontre
 */
public interface MessageService {

    /**
     * Envoie un nouveau message
     */
    MessageResponse sendMessage(CreateMessageRequest request);

    /**
     * Envoie un message rapide avec juste les IDs
     */
    MessageResponse sendMessage(Long senderId, Long receiverId, String content);

    /**
     * Met à jour un message existant (pour édition)
     */
    MessageResponse updateMessage(Long id, UpdateMessageRequest request);

    /**
     * Récupère un message par son ID
     */
    MessageResponse getMessageById(Long id);

    /**
     * Récupère tous les messages d'une conversation entre deux utilisateurs
     */
    List<MessageResponse> getConversation(Long userId1, Long userId2);

    /**
     * Récupère les messages récents d'une conversation (avec pagination)
     */
    List<MessageResponse> getRecentMessagesInConversation(Long userId1, Long userId2, int limit);

    /**
     * Récupère toutes les conversations d'un utilisateur
     */
    List<ConversationResponse> getUserConversations(Long userId);

    /**
     * Marque un message comme lu
     */
    void markMessageAsRead(Long messageId);

    /**
     * Marque tous les messages d'une conversation comme lus
     */
    void markConversationAsRead(Long receiverId, Long senderId);

    /**
     * Supprime un message (suppression logique)
     */
    void deleteMessage(Long messageId, Long userId);

    /**
     * Supprime une conversation entière pour un utilisateur
     */
    void deleteConversation(Long userId, Long otherUserId);

    /**
     * Compte les messages non lus d'un utilisateur
     */
    long countUnreadMessages(Long userId);

    /**
     * Compte les messages non lus d'une conversation spécifique
     */
    long countUnreadMessagesInConversation(Long receiverId, Long senderId);

    /**
     * Récupère les statistiques de messages d'un utilisateur
     */
    MessageStatsResponse getUserMessageStats(Long userId);

    /**
     * Récupère les messages récents pour notifications
     */
    List<MessageResponse> getRecentMessagesForNotifications(Long userId, int hours);

    /**
     * Vérifie si deux utilisateurs ont une conversation existante
     */
    boolean hasConversation(Long userId1, Long userId2);

    /**
     * Recherche dans les messages par contenu
     */
    List<MessageResponse> searchMessages(Long userId, String searchTerm);

    /**
     * Supprime tous les messages d'un utilisateur (suppression de compte)
     */
    void deleteAllMessagesForUser(Long userId);

    /**
     * Vérifie si un utilisateur peut envoyer un message à un autre (match requis)
     */
    boolean canSendMessage(Long senderId, Long receiverId);
}
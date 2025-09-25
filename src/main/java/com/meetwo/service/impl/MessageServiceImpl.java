package com.meetwo.service.impl;

import com.meetwo.dto.mapper.MessageMapper;
import com.meetwo.dto.request.CreateMessageRequest;
import com.meetwo.dto.request.UpdateMessageRequest;
import com.meetwo.dto.response.MessageResponse;
import com.meetwo.dto.response.ConversationResponse;
import com.meetwo.dto.response.MessageStatsResponse;
import com.meetwo.entity.Message;
import com.meetwo.entity.User;
import com.meetwo.exception.message.MessageNotFoundException;
import com.meetwo.exception.message.InvalidMessageOperationException;
import com.meetwo.exception.message.MessageNotAllowedException;
import com.meetwo.exception.user.UserNotFoundException;
import com.meetwo.repository.MessageRepository;
import com.meetwo.repository.UserRepository;
import com.meetwo.service.MessageService;
import com.meetwo.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;
    private final LikeService likeService;

    @Override
    public MessageResponse sendMessage(CreateMessageRequest request) {
        log.info("Envoi d'un message de l'utilisateur {} vers l'utilisateur {}",
                request.getSenderId(), request.getReceiverId());

        // Validation : un utilisateur ne peut pas s'envoyer un message à lui-même
        if (request.getSenderId().equals(request.getReceiverId())) {
            throw new InvalidMessageOperationException("Un utilisateur ne peut pas s'envoyer un message à lui-même");
        }

        // Vérifier que les utilisateurs existent
        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new UserNotFoundException(request.getSenderId()));
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new UserNotFoundException(request.getReceiverId()));

        // Vérifier que l'utilisateur peut envoyer un message (match requis)
        if (!canSendMessage(request.getSenderId(), request.getReceiverId())) {
            throw new MessageNotAllowedException(request.getSenderId(), request.getReceiverId());
        }

        // Créer le message
        Message message = messageMapper.toEntity(request, sender, receiver);
        Message savedMessage = messageRepository.save(message);

        log.info("Message créé avec l'ID {} de {} vers {}",
                savedMessage.getId(), request.getSenderId(), request.getReceiverId());

        return messageMapper.toResponse(savedMessage);
    }

    @Override
    public MessageResponse sendMessage(Long senderId, Long receiverId, String content) {
        CreateMessageRequest request = new CreateMessageRequest();
        request.setSenderId(senderId);
        request.setReceiverId(receiverId);
        request.setContent(content);
        return sendMessage(request);
    }

    @Override
    public MessageResponse updateMessage(Long id, UpdateMessageRequest request) {
        log.info("Mise à jour du message avec l'ID {}", id);

        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new MessageNotFoundException(id));

        messageMapper.updateEntity(message, request);
        Message updatedMessage = messageRepository.save(message);

        return messageMapper.toResponse(updatedMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageResponse getMessageById(Long id) {
        log.debug("Récupération du message avec l'ID {}", id);

        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new MessageNotFoundException(id));

        return messageMapper.toResponse(message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getConversation(Long userId1, Long userId2) {
        log.debug("Récupération de la conversation entre {} et {}", userId1, userId2);

        if (!userRepository.existsById(userId1)) {
            throw new UserNotFoundException(userId1);
        }
        if (!userRepository.existsById(userId2)) {
            throw new UserNotFoundException(userId2);
        }

        return messageRepository.findConversationBetweenUsers(userId1, userId2).stream()
                .filter(message -> message.isVisibleForUser(userId1) || message.isVisibleForUser(userId2))
                .map(messageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getRecentMessagesInConversation(Long userId1, Long userId2, int limit) {
        log.debug("Récupération des {} derniers messages entre {} et {}", limit, userId1, userId2);

        return messageRepository.findRecentMessagesBetweenUsers(userId1, userId2, limit).stream()
                .map(messageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> getUserConversations(Long userId) {
        log.debug("Récupération des conversations de l'utilisateur {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        List<Long> conversationPartnerIds = messageRepository.findConversationPartnerIds(userId);
        List<ConversationResponse> conversations = new ArrayList<>();

        for (Long partnerId : conversationPartnerIds) {
            User partner = userRepository.findById(partnerId)
                    .orElse(null);

            if (partner != null) {
                MessageResponse lastMessage = messageRepository
                        .findLastMessageBetweenUsers(userId, partnerId)
                        .map(messageMapper::toResponse)
                        .orElse(null);

                long unreadCount = messageRepository.countUnreadMessagesInConversation(userId, partnerId);

                List<MessageResponse> recentMessages = getRecentMessagesInConversation(userId, partnerId, 5);

                ConversationResponse conversation = messageMapper.toConversationResponse(
                        partner, lastMessage, unreadCount, recentMessages);

                conversations.add(conversation);
            }
        }

        // Trier par date du dernier message
        conversations.sort((c1, c2) -> {
            LocalDateTime date1 = c1.getLastMessageAt();
            LocalDateTime date2 = c2.getLastMessageAt();
            if (date1 == null && date2 == null) return 0;
            if (date1 == null) return 1;
            if (date2 == null) return -1;
            return date2.compareTo(date1);
        });

        return conversations;
    }

    @Override
    public void markMessageAsRead(Long messageId) {
        log.info("Marquage du message {} comme lu", messageId);

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));

        message.markAsRead();
        messageRepository.save(message);
    }

    @Override
    public void markConversationAsRead(Long receiverId, Long senderId) {
        log.info("Marquage de la conversation comme lue pour {} (messages de {})", receiverId, senderId);

        messageRepository.markConversationAsRead(receiverId, senderId, LocalDateTime.now());
        log.info("Conversation marquée comme lue");
    }

    @Override
    public void deleteMessage(Long messageId, Long userId) {
        log.info("Suppression du message {} par l'utilisateur {}", messageId, userId);

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(messageId));

        // Marquer comme supprimé du côté approprié
        if (message.getSender().getId().equals(userId)) {
            message.setIsDeletedBySender(true);
        } else if (message.getReceiver().getId().equals(userId)) {
            message.setIsDeletedByReceiver(true);
        } else {
            throw new InvalidMessageOperationException("L'utilisateur n'est pas autorisé à supprimer ce message");
        }

        messageRepository.save(message);

        // Si le message est supprimé des deux côtés, le supprimer physiquement
        if (message.getIsDeletedBySender() && message.getIsDeletedByReceiver()) {
            messageRepository.delete(message);
            log.info("Message {} supprimé physiquement", messageId);
        }
    }

    @Override
    public void deleteConversation(Long userId, Long otherUserId) {
        log.info("Suppression de la conversation pour {} avec {}", userId, otherUserId);

        if (!userRepository.existsById(userId) || !userRepository.existsById(otherUserId)) {
            throw new UserNotFoundException("Un des utilisateurs n'existe pas");
        }

        messageRepository.markMessagesAsDeletedBySender(userId, otherUserId);
        messageRepository.markMessagesAsDeletedByReceiver(userId, otherUserId);

        log.info("Conversation supprimée pour l'utilisateur {}", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnreadMessages(Long userId) {
        return messageRepository.countUnreadMessagesByReceiver(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnreadMessagesInConversation(Long receiverId, Long senderId) {
        return messageRepository.countUnreadMessagesInConversation(receiverId, senderId);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageStatsResponse getUserMessageStats(Long userId) {
        log.debug("Récupération des statistiques de messages pour l'utilisateur {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        long messagesSent = messageRepository.countBySenderIdAndIsDeletedBySenderFalse(userId);
        long messagesReceived = messageRepository.countByReceiverIdAndIsDeletedByReceiverFalse(userId);
        long unreadMessages = messageRepository.countUnreadMessagesByReceiver(userId);
        long activeConversations = messageRepository.countActiveConversationsByUser(userId);

        // Calcul du temps de réponse moyen (simplifié)
        double averageResponseTime = calculateAverageResponseTime(userId);

        return messageMapper.toStatsResponse(userId, messagesSent, messagesReceived,
                unreadMessages, activeConversations, averageResponseTime);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getRecentMessagesForNotifications(Long userId, int hours) {
        log.debug("Récupération des messages récents pour {} dans les dernières {} heures", userId, hours);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        LocalDateTime since = LocalDateTime.now().minusHours(hours);

        return messageRepository.findRecentMessagesForUser(userId, since).stream()
                .map(messageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasConversation(Long userId1, Long userId2) {
        return messageRepository.existsConversationBetweenUsers(userId1, userId2);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> searchMessages(Long userId, String searchTerm) {
        log.debug("Recherche de messages pour l'utilisateur {} avec le terme '{}'", userId, searchTerm);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        return messageRepository.searchMessagesByContent(userId, searchTerm).stream()
                .map(messageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllMessagesForUser(Long userId) {
        log.info("Suppression de tous les messages de l'utilisateur {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        messageRepository.deleteBySenderIdOrReceiverId(userId, userId);
        log.info("Tous les messages de l'utilisateur {} ont été supprimés", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canSendMessage(Long senderId, Long receiverId) {
        // Vérifier s'il y a un match mutuel entre les deux utilisateurs
        return likeService.isMatch(senderId, receiverId);
    }

    // === MÉTHODES UTILITAIRES PRIVÉES ===

    /**
     * Calcule le temps de réponse moyen en minutes (méthode simplifiée)
     */
    private double calculateAverageResponseTime(Long userId) {
        // Cette implémentation est simplifiée
        // Dans un vrai système, on analyserait les timestamps des conversations
        // pour calculer le temps entre la réception d'un message et la réponse

        List<Message> recentSentMessages = messageRepository
                .findBySenderIdAndIsDeletedBySenderFalseOrderByCreatedAtDesc(userId);

        if (recentSentMessages.size() < 2) {
            return 0.0;
        }

        long totalMinutes = 0;
        int responseCount = 0;

        for (int i = 0; i < Math.min(recentSentMessages.size() - 1, 10); i++) {
            Message current = recentSentMessages.get(i);
            Message next = recentSentMessages.get(i + 1);

            if (current.getReceiver().getId().equals(next.getReceiver().getId())) {
                long minutes = ChronoUnit.MINUTES.between(next.getCreatedAt(), current.getCreatedAt());
                if (minutes > 0 && minutes < 1440) { // Moins de 24 heures
                    totalMinutes += minutes;
                    responseCount++;
                }
            }
        }

        return responseCount > 0 ? (double) totalMinutes / responseCount : 0.0;
    }
}
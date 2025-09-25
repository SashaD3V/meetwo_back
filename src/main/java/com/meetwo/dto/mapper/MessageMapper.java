package com.meetwo.dto.mapper;

import com.meetwo.dto.request.CreateMessageRequest;
import com.meetwo.dto.request.UpdateMessageRequest;
import com.meetwo.dto.response.MessageResponse;
import com.meetwo.dto.response.ConversationResponse;
import com.meetwo.dto.response.MessageStatsResponse;
import com.meetwo.entity.Message;
import com.meetwo.entity.User;
import com.meetwo.enums.MessageType;
import com.meetwo.service.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageMapper {

    private final PhotoService photoService;

    public Message toEntity(CreateMessageRequest request, User sender, User receiver) {
        return Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(request.getContent())
                .messageType(request.getMessageType() != null ? request.getMessageType() : MessageType.TEXT)
                .isRead(false)
                .build();
    }

    public MessageResponse toResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                // Informations de l'expéditeur
                .senderId(message.getSender().getId())
                .senderUsername(message.getSender().getUsername())
                .senderName(message.getSender().getName())
                .senderMainPhotoUrl(getMainPhotoUrl(message.getSender().getId()))
                // Informations du destinataire
                .receiverId(message.getReceiver().getId())
                .receiverUsername(message.getReceiver().getUsername())
                .receiverName(message.getReceiver().getName())
                .receiverMainPhotoUrl(getMainPhotoUrl(message.getReceiver().getId()))
                // Contenu et métadonnées
                .content(message.getContent())
                .isRead(message.getIsRead())
                .messageType(message.getMessageType())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .readAt(message.getReadAt())
                .build();
    }

    public ConversationResponse toConversationResponse(User conversationWith, MessageResponse lastMessage,
                                                       long unreadCount, List<MessageResponse> recentMessages) {
        return ConversationResponse.builder()
                .conversationWithUserId(conversationWith.getId())
                .conversationWithUsername(conversationWith.getUsername())
                .conversationWithName(conversationWith.getName())
                .conversationWithMainPhotoUrl(getMainPhotoUrl(conversationWith.getId()))
                .lastMessage(lastMessage)
                .unreadCount(unreadCount)
                .lastMessageAt(lastMessage != null ? lastMessage.getCreatedAt() : null)
                .recentMessages(recentMessages)
                .build();
    }

    public MessageStatsResponse toStatsResponse(Long userId, long messagesSent, long messagesReceived,
                                                long unreadMessages, long activeConversations,
                                                double averageResponseTime) {
        return MessageStatsResponse.builder()
                .userId(userId)
                .totalMessagesSent(messagesSent)
                .totalMessagesReceived(messagesReceived)
                .totalUnreadMessages(unreadMessages)
                .activeConversations(activeConversations)
                .averageResponseTime(averageResponseTime)
                .build();
    }

    public void updateEntity(Message message, UpdateMessageRequest request) {
        if (request == null || message == null) {
            log.warn("Tentative de mise à jour d'un message avec des paramètres null");
            return;
        }

        if (request.getContent() != null) {
            message.setContent(request.getContent());
        }
    }

    private String getMainPhotoUrl(Long userId) {
        try {
            return photoService.getMainPhotoUrl(userId);
        } catch (Exception e) {
            log.debug("Aucune photo principale trouvée pour l'utilisateur {}: {}", userId, e.getMessage());
            return null; // Si pas de photo principale
        }
    }
}
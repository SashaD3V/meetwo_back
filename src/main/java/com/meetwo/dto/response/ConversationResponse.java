package com.meetwo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {
    private Long conversationWithUserId;
    private String conversationWithUsername;
    private String conversationWithName;
    private String conversationWithMainPhotoUrl;
    private MessageResponse lastMessage;
    private long unreadCount; // Nombre de messages non lus
    private LocalDateTime lastMessageAt; // Date du dernier message
    private List<MessageResponse> recentMessages; // Les derniers messages (optionnel)
}
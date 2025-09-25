package com.meetwo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageStatsResponse {
    private Long userId;
    private long totalMessagesSent; // Nombre total de messages envoyés
    private long totalMessagesReceived; // Nombre total de messages reçus
    private long totalUnreadMessages; // Nombre de messages non lus
    private long activeConversations; // Nombre de conversations actives
    private double averageResponseTime; // Temps de réponse moyen en minutes
}
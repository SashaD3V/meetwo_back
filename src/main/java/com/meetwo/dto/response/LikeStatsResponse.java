package com.meetwo.dto.response;

import lombok.Data;

@Data
public class LikeStatsResponse {
    private Long userId;
    private long likesGiven; // Nombre de likes donnés
    private long likesReceived; // Nombre de likes reçus
    private long matchesCount; // Nombre de matches mutuels
    private double likeBackRate; // Pourcentage de likes retournés (matches / likes donnés)
    private double popularityScore; // Score de popularité basé sur les likes reçus
}
package com.meetwo.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MatchResponse {
    private Long matchedUserId;
    private String username;
    private String name;
    private Integer age;
    private String city;
    private String biography;
    private String mainPhotoUrl;
    private LocalDateTime matchedAt; // Date du match (plus récent des deux likes)
    private boolean hasUnreadMessages; // Pour les futures fonctionnalités de chat
}
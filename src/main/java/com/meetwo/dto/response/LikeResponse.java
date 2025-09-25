package com.meetwo.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LikeResponse {
    private Long id;
    private Long likerId;
    private String likerUsername;
    private String likerName;
    private String likerMainPhotoUrl;
    private Long likedUserId;
    private String likedUserUsername;
    private String likedUserName;
    private String likedUserMainPhotoUrl;
    private boolean isMatch; // Indique si c'est un match mutuel
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
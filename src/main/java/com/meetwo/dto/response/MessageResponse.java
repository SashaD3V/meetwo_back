package com.meetwo.dto.response;

import com.meetwo.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private Long id;
    private Long senderId;
    private String senderUsername;
    private String senderName;
    private String senderMainPhotoUrl;
    private Long receiverId;
    private String receiverUsername;
    private String receiverName;
    private String receiverMainPhotoUrl;
    private String content;
    private Boolean isRead;
    private MessageType messageType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime readAt;
}
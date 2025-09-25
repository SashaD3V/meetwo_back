package com.meetwo.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PhotoResponse {
    private Long id;
    private Long userId;
    private String url;
    private Integer position;
    private Boolean estPrincipale;
    private String altText;
    private Long fileSize;
    private Integer width;
    private Integer height;
    private String contentType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

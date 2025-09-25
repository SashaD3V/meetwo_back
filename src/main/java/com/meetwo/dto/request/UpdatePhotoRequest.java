package com.meetwo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePhotoRequest {

    @NotBlank(message = "L'URL ne peut pas être vide")
    private String url;

    @Min(value = 1, message = "La position doit être au minimum 1")
    private Integer position;

    private Boolean estPrincipale;

    private String altText;

    private Long fileSize;

    private Integer width;

    private Integer height;

    private String contentType;
}

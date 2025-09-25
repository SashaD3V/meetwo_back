package com.meetwo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePhotoRequest {

    @NotNull(message = "L'ID utilisateur est requis")
    private Long userId;

    @NotBlank(message = "L'URL de la photo est requise")
    private String url;

    @NotNull(message = "La position est requise")
    @Min(value = 1, message = "La position doit être au minimum 1")
    private Integer position;

    private Boolean estPrincipale = false;
    private String altText;
    private Long fileSize;
    private Integer width;
    private Integer height;
    private String contentType;
}
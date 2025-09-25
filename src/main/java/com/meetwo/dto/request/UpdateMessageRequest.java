package com.meetwo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMessageRequest {

    @NotBlank(message = "Le contenu ne peut pas être vide")
    @Size(max = 1000, message = "Le message ne peut pas dépasser 1000 caractères")
    private String content;

    // Pour futures fonctionnalités (édition de message)
    private String editReason;
}
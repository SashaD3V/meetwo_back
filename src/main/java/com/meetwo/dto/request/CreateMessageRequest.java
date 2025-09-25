package com.meetwo.dto.request;

import com.meetwo.enums.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Données requises pour créer un nouveau message")
public class CreateMessageRequest {

    @NotNull(message = "L'ID de l'expéditeur est requis")
    @Schema(description = "ID de l'utilisateur qui envoie le message", example = "1")
    private Long senderId;

    @NotNull(message = "L'ID du destinataire est requis")
    @Schema(description = "ID de l'utilisateur qui reçoit le message", example = "2")
    private Long receiverId;

    @NotBlank(message = "Le contenu du message ne peut pas être vide")
    @Size(max = 1000, message = "Le message ne peut pas dépasser 1000 caractères")
    @Schema(description = "Contenu du message", example = "Salut ! Comment ça va ?")
    private String content;

    @Builder.Default
    @Schema(description = "Type de message", example = "TEXT", allowableValues = {"TEXT", "IMAGE", "SYSTEM"})
    private MessageType messageType = MessageType.TEXT;
}
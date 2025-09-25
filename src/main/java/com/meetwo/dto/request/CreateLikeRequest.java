package com.meetwo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Données requises pour créer un nouveau like")
public class CreateLikeRequest {

    @NotNull(message = "L'ID de l'utilisateur qui like est requis")
    @Schema(description = "ID de l'utilisateur qui donne le like", example = "1")
    private Long likerId;

    @NotNull(message = "L'ID de l'utilisateur liké est requis")
    @Schema(description = "ID de l'utilisateur qui reçoit le like", example = "2")
    private Long likedUserId;
}
package com.meetwo.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Genre de l'utilisateur")
public enum Gender {
    @Schema(description = "Homme")
    HOMME,

    @Schema(description = "Femme")
    FEMME
}

package com.meetwo.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Type de message")
public enum MessageType {
    @Schema(description = "Message texte standard")
    TEXT,

    @Schema(description = "Message contenant une image")
    IMAGE,

    @Schema(description = "Message système automatique")
    SYSTEM
}
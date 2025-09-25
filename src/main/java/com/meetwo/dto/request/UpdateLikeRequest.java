package com.meetwo.dto.request;

import lombok.Data;

@Data
public class UpdateLikeRequest {

    // Dans le contexte des likes, il n'y a pas grand chose à mettre à jour
    // car un like est soit présent, soit absent
    // Mais on peut garder cette classe pour la cohérence et d'éventuelles futures fonctionnalités

    // Métadonnées optionnelles (pour futures fonctionnalités)
    private String note; // Note privée sur ce like (future fonctionnalité)
    private Boolean isHidden; // Masquer ce like (future fonctionnalité)

    // Pour l'instant, cette classe reste simple mais extensible
}
package com.meetwo.controller;

import com.meetwo.dto.request.CreateMessageRequest;
import com.meetwo.dto.request.UpdateMessageRequest;
import com.meetwo.dto.response.MessageResponse;
import com.meetwo.dto.response.ConversationResponse;
import com.meetwo.dto.response.MessageStatsResponse;
import com.meetwo.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Messages", description = "API de gestion des messages dans l'application de rencontre")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    @Operation(summary = "Envoyer un message", description = "Permet à un utilisateur d'envoyer un message à un autre utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message envoyé avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "Match requis pour envoyer un message"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody CreateMessageRequest request) {
        MessageResponse message = messageService.sendMessage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @PostMapping("/quick")
    @Operation(summary = "Message rapide", description = "Permet d'envoyer rapidement un message avec les IDs en paramètres")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message envoyé avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Paramètres invalides"),
            @ApiResponse(responseCode = "403", description = "Match requis pour envoyer un message"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<MessageResponse> sendQuickMessage(
            @Parameter(description = "ID de l'expéditeur", required = true)
            @RequestParam Long senderId,
            @Parameter(description = "ID du destinataire", required = true)
            @RequestParam Long receiverId,
            @Parameter(description = "Contenu du message", required = true)
            @RequestParam String content) {
        MessageResponse message = messageService.sendMessage(senderId, receiverId, content);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un message", description = "Met à jour le contenu d'un message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message modifié avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "404", description = "Message non trouvé"),
            @ApiResponse(responseCode = "400", description = "Données d'entrée invalides")
    })
    public ResponseEntity<MessageResponse> updateMessage(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMessageRequest request) {
        MessageResponse message = messageService.updateMessage(id, request);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un message", description = "Récupère un message par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message trouvé",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "404", description = "Message non trouvé")
    })
    public ResponseEntity<MessageResponse> getMessageById(@PathVariable Long id) {
        MessageResponse message = messageService.getMessageById(id);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/conversation")
    @Operation(summary = "Récupérer une conversation",
            description = "Récupère tous les messages entre deux utilisateurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversation récupérée",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<List<MessageResponse>> getConversation(
            @Parameter(description = "ID du premier utilisateur", required = true)
            @RequestParam Long userId1,
            @Parameter(description = "ID du second utilisateur", required = true)
            @RequestParam Long userId2) {
        List<MessageResponse> messages = messageService.getConversation(userId1, userId2);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/conversation/recent")
    @Operation(summary = "Messages récents d'une conversation",
            description = "Récupère les derniers messages d'une conversation avec pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages récupérés",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<List<MessageResponse>> getRecentMessagesInConversation(
            @Parameter(description = "ID du premier utilisateur", required = true)
            @RequestParam Long userId1,
            @Parameter(description = "ID du second utilisateur", required = true)
            @RequestParam Long userId2,
            @Parameter(description = "Nombre de messages à récupérer", example = "20")
            @RequestParam(defaultValue = "20") int limit) {
        List<MessageResponse> messages = messageService.getRecentMessagesInConversation(userId1, userId2, limit);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/conversations/user/{userId}")
    @Operation(summary = "Conversations d'un utilisateur",
            description = "Récupère toutes les conversations d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversations récupérées",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<List<ConversationResponse>> getUserConversations(@PathVariable Long userId) {
        List<ConversationResponse> conversations = messageService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Marquer un message comme lu", description = "Marque un message comme lu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message marqué comme lu"),
            @ApiResponse(responseCode = "404", description = "Message non trouvé")
    })
    public ResponseEntity<Void> markMessageAsRead(@PathVariable Long id) {
        messageService.markMessageAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/conversation/read")
    @Operation(summary = "Marquer une conversation comme lue",
            description = "Marque tous les messages d'une conversation comme lus")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversation marquée comme lue"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<Void> markConversationAsRead(
            @Parameter(description = "ID du destinataire", required = true)
            @RequestParam Long receiverId,
            @Parameter(description = "ID de l'expéditeur", required = true)
            @RequestParam Long senderId) {
        messageService.markConversationAsRead(receiverId, senderId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un message", description = "Supprime un message pour l'utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Message supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Message non trouvé"),
            @ApiResponse(responseCode = "403", description = "Non autorisé à supprimer ce message")
    })
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long id,
            @Parameter(description = "ID de l'utilisateur qui supprime", required = true)
            @RequestParam Long userId) {
        messageService.deleteMessage(id, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/conversation")
    @Operation(summary = "Supprimer une conversation",
            description = "Supprime une conversation entière pour un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Conversation supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<Void> deleteConversation(
            @Parameter(description = "ID de l'utilisateur", required = true)
            @RequestParam Long userId,
            @Parameter(description = "ID de l'autre utilisateur", required = true)
            @RequestParam Long otherUserId) {
        messageService.deleteConversation(userId, otherUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unread/count/{userId}")
    @Operation(summary = "Nombre de messages non lus",
            description = "Compte le nombre total de messages non lus d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nombre récupéré",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Long.class)))
    })
    public ResponseEntity<Long> countUnreadMessages(@PathVariable Long userId) {
        long count = messageService.countUnreadMessages(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/unread/count/conversation")
    @Operation(summary = "Messages non lus d'une conversation",
            description = "Compte les messages non lus d'une conversation spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nombre récupéré",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Long.class)))
    })
    public ResponseEntity<Long> countUnreadMessagesInConversation(
            @Parameter(description = "ID du destinataire", required = true)
            @RequestParam Long receiverId,
            @Parameter(description = "ID de l'expéditeur", required = true)
            @RequestParam Long senderId) {
        long count = messageService.countUnreadMessagesInConversation(receiverId, senderId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/user/{userId}")
    @Operation(summary = "Statistiques de messages",
            description = "Récupère les statistiques de messages d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques récupérées",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageStatsResponse.class))),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<MessageStatsResponse> getUserMessageStats(@PathVariable Long userId) {
        MessageStatsResponse stats = messageService.getUserMessageStats(userId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/recent/user/{userId}")
    @Operation(summary = "Messages récents pour notifications",
            description = "Récupère les messages récents reçus par un utilisateur (pour notifications)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages récents récupérés",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<List<MessageResponse>> getRecentMessagesForNotifications(
            @PathVariable Long userId,
            @Parameter(description = "Nombre d'heures à considérer", example = "24")
            @RequestParam(defaultValue = "24") int hours) {
        List<MessageResponse> recentMessages = messageService.getRecentMessagesForNotifications(userId, hours);
        return ResponseEntity.ok(recentMessages);
    }

    @GetMapping("/conversation/exists")
    @Operation(summary = "Vérifier l'existence d'une conversation",
            description = "Vérifie si une conversation existe entre deux utilisateurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vérification effectuée",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class)))
    })
    public ResponseEntity<Boolean> hasConversation(
            @Parameter(description = "ID du premier utilisateur", required = true)
            @RequestParam Long userId1,
            @Parameter(description = "ID du second utilisateur", required = true)
            @RequestParam Long userId2) {
        boolean hasConversation = messageService.hasConversation(userId1, userId2);
        return ResponseEntity.ok(hasConversation);
    }

    @GetMapping("/search/user/{userId}")
    @Operation(summary = "Rechercher dans les messages",
            description = "Recherche dans les messages d'un utilisateur par contenu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Résultats de recherche récupérés",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<List<MessageResponse>> searchMessages(
            @PathVariable Long userId,
            @Parameter(description = "Terme de recherche", required = true)
            @RequestParam String searchTerm) {
        List<MessageResponse> messages = messageService.searchMessages(userId, searchTerm);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/can-send")
    @Operation(summary = "Vérifier si un message peut être envoyé",
            description = "Vérifie si un utilisateur peut envoyer un message à un autre (match requis)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vérification effectuée",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class)))
    })
    public ResponseEntity<Boolean> canSendMessage(
            @Parameter(description = "ID de l'expéditeur", required = true)
            @RequestParam Long senderId,
            @Parameter(description = "ID du destinataire", required = true)
            @RequestParam Long receiverId) {
        boolean canSend = messageService.canSendMessage(senderId, receiverId);
        return ResponseEntity.ok(canSend);
    }

    @DeleteMapping("/user/{userId}/all")
    @Operation(summary = "Supprimer tous les messages d'un utilisateur",
            description = "Supprime tous les messages d'un utilisateur (lors de suppression de compte)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tous les messages supprimés"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<Void> deleteAllMessagesForUser(@PathVariable Long userId) {
        messageService.deleteAllMessagesForUser(userId);
        return ResponseEntity.noContent().build();
    }
}
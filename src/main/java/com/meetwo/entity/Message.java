package com.meetwo.entity;

import com.meetwo.enums.MessageType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_message")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender; // L'utilisateur qui envoie le message

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver; // L'utilisateur qui reçoit le message

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // Contenu du message

    @Builder.Default
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false; // Message lu ou non

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "read_at")
    private LocalDateTime readAt; // Date de lecture du message

    // Métadonnées optionnelles
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    private MessageType messageType = MessageType.TEXT;

    @Builder.Default
    @Column(name = "is_deleted_by_sender")
    private Boolean isDeletedBySender = false;

    @Builder.Default
    @Column(name = "is_deleted_by_receiver")
    private Boolean isDeletedByReceiver = false;

    // Constructeur utilitaire
    public Message(User sender, User receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.isRead = false;
        this.messageType = MessageType.TEXT;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeletedBySender = false;
        this.isDeletedByReceiver = false;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (isRead == null) {
            isRead = false;
        }
        if (messageType == null) {
            messageType = MessageType.TEXT;
        }
        if (isDeletedBySender == null) {
            isDeletedBySender = false;
        }
        if (isDeletedByReceiver == null) {
            isDeletedByReceiver = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Méthode utilitaire pour marquer comme lu
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    // Méthode utilitaire pour vérifier si le message est visible pour un utilisateur
    public boolean isVisibleForUser(Long userId) {
        if (sender.getId().equals(userId)) {
            return !isDeletedBySender;
        } else if (receiver.getId().equals(userId)) {
            return !isDeletedByReceiver;
        }
        return false;
    }
}
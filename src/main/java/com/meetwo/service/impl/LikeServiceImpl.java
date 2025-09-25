package com.meetwo.service.impl;

import com.meetwo.dto.mapper.LikeMapper;
import com.meetwo.dto.request.CreateLikeRequest;
import com.meetwo.dto.request.UpdateLikeRequest;
import com.meetwo.dto.response.LikeResponse;
import com.meetwo.dto.response.MatchResponse;
import com.meetwo.dto.response.LikeStatsResponse;
import com.meetwo.entity.Like;
import com.meetwo.entity.User;
import com.meetwo.exception.like.LikeAlreadyExistsException;
import com.meetwo.exception.like.LikeNotFoundException;
import com.meetwo.exception.like.SelfLikeNotAllowedException;
import com.meetwo.exception.user.UserNotFoundException;
import com.meetwo.repository.LikeRepository;
import com.meetwo.repository.UserRepository;
import com.meetwo.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final LikeMapper likeMapper;

    @Override
    public LikeResponse createLike(CreateLikeRequest request) {
        log.info("Création d'un like de l'utilisateur {} vers l'utilisateur {}",
                request.getLikerId(), request.getLikedUserId());

        // Validation : un utilisateur ne peut pas se liker lui-même
        if (request.getLikerId().equals(request.getLikedUserId())) {
            throw new SelfLikeNotAllowedException(request.getLikerId());
        }

        // Vérifier que les utilisateurs existent
        User liker = userRepository.findById(request.getLikerId())
                .orElseThrow(() -> new UserNotFoundException(request.getLikerId()));
        User likedUser = userRepository.findById(request.getLikedUserId())
                .orElseThrow(() -> new UserNotFoundException(request.getLikedUserId()));

        // Vérifier que le like n'existe pas déjà
        if (likeRepository.existsByLikerIdAndLikedUserId(request.getLikerId(), request.getLikedUserId())) {
            throw new LikeAlreadyExistsException(request.getLikerId(), request.getLikedUserId());
        }

        // Créer le like
        Like like = likeMapper.toEntity(request, liker, likedUser);
        Like savedLike = likeRepository.save(like);

        // Vérifier si c'est un match mutuel
        boolean isMatch = likeRepository.existsByLikerIdAndLikedUserId(
                request.getLikedUserId(), request.getLikerId());

        if (isMatch) {
            log.info("Match détecté entre les utilisateurs {} et {}",
                    request.getLikerId(), request.getLikedUserId());
        }

        log.info("Like créé avec l'ID {} (Match: {})", savedLike.getId(), isMatch);
        return likeMapper.toResponseWithMatch(savedLike, isMatch);
    }

    @Override
    public LikeResponse likeUser(Long likerId, Long likedUserId) {
        CreateLikeRequest request = new CreateLikeRequest();
        request.setLikerId(likerId);
        request.setLikedUserId(likedUserId);
        return createLike(request);
    }

    @Override
    public LikeResponse updateLike(Long id, UpdateLikeRequest request) {
        log.info("Mise à jour du like avec l'ID {}", id);

        Like like = likeRepository.findById(id)
                .orElseThrow(() -> new LikeNotFoundException(id));

        likeMapper.updateEntity(like, request);
        Like updatedLike = likeRepository.save(like);

        // Vérifier si c'est toujours un match
        boolean isMatch = likeRepository.existsByLikerIdAndLikedUserId(
                like.getLikedUser().getId(), like.getLiker().getId());

        return likeMapper.toResponseWithMatch(updatedLike, isMatch);
    }

    @Override
    @Transactional(readOnly = true)
    public LikeResponse getLikeById(Long id) {
        log.debug("Récupération du like avec l'ID {}", id);

        Like like = likeRepository.findById(id)
                .orElseThrow(() -> new LikeNotFoundException(id));

        // Vérifier si c'est un match
        boolean isMatch = likeRepository.existsByLikerIdAndLikedUserId(
                like.getLikedUser().getId(), like.getLiker().getId());

        return likeMapper.toResponseWithMatch(like, isMatch);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LikeResponse> getLikesGivenByUser(Long userId) {
        log.debug("Récupération des likes donnés par l'utilisateur {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        return likeRepository.findByLikerIdOrderByCreatedAtDesc(userId).stream()
                .map(like -> {
                    boolean isMatch = likeRepository.existsByLikerIdAndLikedUserId(
                            like.getLikedUser().getId(), like.getLiker().getId());
                    return likeMapper.toResponseWithMatch(like, isMatch);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LikeResponse> getLikesReceivedByUser(Long userId) {
        log.debug("Récupération des likes reçus par l'utilisateur {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        return likeRepository.findByLikedUserIdOrderByCreatedAtDesc(userId).stream()
                .map(like -> {
                    boolean isMatch = likeRepository.existsByLikerIdAndLikedUserId(
                            like.getLikedUser().getId(), like.getLiker().getId());
                    return likeMapper.toResponseWithMatch(like, isMatch);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResponse> getMatchesByUser(Long userId) {
        log.debug("Récupération des matches de l'utilisateur {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        List<Like> mutualLikes = likeRepository.findMutualLikesByUser(userId);

        return mutualLikes.stream()
                .map(like -> {
                    // Déterminer l'utilisateur matché (l'autre que userId)
                    User matchedUser = like.getLiker().getId().equals(userId)
                            ? like.getLikedUser()
                            : like.getLiker();

                    // Trouver la date du match (le plus récent des deux likes)
                    LocalDateTime matchedAt = findMatchDate(userId, matchedUser.getId());

                    return likeMapper.toMatchResponse(matchedUser, matchedAt);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMatch(Long userId1, Long userId2) {
        return likeRepository.existsByLikerIdAndLikedUserId(userId1, userId2) &&
                likeRepository.existsByLikerIdAndLikedUserId(userId2, userId1);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserLiked(Long likerId, Long likedUserId) {
        return likeRepository.existsByLikerIdAndLikedUserId(likerId, likedUserId);
    }

    @Override
    public void removeLike(Long likerId, Long likedUserId) {
        log.info("Suppression du like de l'utilisateur {} vers l'utilisateur {}", likerId, likedUserId);

        if (!likeRepository.existsByLikerIdAndLikedUserId(likerId, likedUserId)) {
            throw new LikeNotFoundException(likerId, likedUserId);
        }

        likeRepository.deleteByLikerIdAndLikedUserId(likerId, likedUserId);
        log.info("Like supprimé entre les utilisateurs {} et {}", likerId, likedUserId);
    }

    @Override
    public void removeLike(Long likeId) {
        log.info("Suppression du like avec l'ID {}", likeId);

        if (!likeRepository.existsById(likeId)) {
            throw new LikeNotFoundException(likeId);
        }

        likeRepository.deleteById(likeId);
        log.info("Like {} supprimé", likeId);
    }

    @Override
    @Transactional(readOnly = true)
    public LikeStatsResponse getUserLikeStats(Long userId) {
        log.debug("Récupération des statistiques de likes pour l'utilisateur {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        long likesGiven = likeRepository.countByLikerId(userId);
        long likesReceived = likeRepository.countByLikedUserId(userId);
        long matchesCount = countMatchesByUser(userId);

        return likeMapper.toStatsResponse(userId, likesGiven, likesReceived, matchesCount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getLikedUserIds(Long likerId) {
        log.debug("Récupération des IDs des utilisateurs likés par {}", likerId);
        return likeRepository.findLikedUserIdsByLiker(likerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResponse> getTopLikedUsers(int limit) {
        log.debug("Récupération des {} utilisateurs les plus likés", limit);

        List<Object[]> topLiked = likeRepository.findTopLikedUsers(limit);

        return topLiked.stream()
                .map(result -> {
                    Long userId = (Long) result[0];
                    Long likeCount = (Long) result[1];

                    User user = userRepository.findById(userId)
                            .orElse(null);

                    if (user != null) {
                        MatchResponse response = likeMapper.toMatchResponse(user, LocalDateTime.now());
                        // On pourrait ajouter le nombre de likes dans une extension du DTO
                        return response;
                    }
                    return null;
                })
                .filter(response -> response != null)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LikeResponse> getRecentLikesReceived(Long userId, int hours) {
        log.debug("Récupération des likes reçus par {} dans les dernières {} heures", userId, hours);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        LocalDateTime since = LocalDateTime.now().minusHours(hours);

        return likeRepository.findRecentLikesReceived(userId, since).stream()
                .map(like -> {
                    boolean isMatch = likeRepository.existsByLikerIdAndLikedUserId(
                            like.getLikedUser().getId(), like.getLiker().getId());
                    return likeMapper.toResponseWithMatch(like, isMatch);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countLikesGivenByUser(Long userId) {
        return likeRepository.countByLikerId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countLikesReceivedByUser(Long userId) {
        return likeRepository.countByLikedUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countMatchesByUser(Long userId) {
        return likeRepository.findMutualLikesByUser(userId).size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResponse> getMatchesByCity(String city) {
        log.debug("Récupération des matches dans la ville {}", city);

        return likeRepository.findLikesByCity(city).stream()
                .filter(like -> isMatch(like.getLiker().getId(), like.getLikedUser().getId()))
                .map(like -> {
                    LocalDateTime matchedAt = findMatchDate(like.getLiker().getId(), like.getLikedUser().getId());
                    return likeMapper.toMatchResponse(like, matchedAt);
                })
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public void removeAllLikesForUser(Long userId) {
        log.info("Suppression de tous les likes de l'utilisateur {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        likeRepository.deleteByLikerIdOrLikedUserId(userId, userId);
        log.info("Tous les likes de l'utilisateur {} ont été supprimés", userId);
    }

    // === MÉTHODES UTILITAIRES PRIVÉES ===

    /**
     * Trouve la date du match entre deux utilisateurs (le plus récent des deux likes)
     */
    private LocalDateTime findMatchDate(Long userId1, Long userId2) {
        LocalDateTime date1 = likeRepository.findByLikerIdAndLikedUserId(userId1, userId2)
                .map(Like::getCreatedAt)
                .orElse(LocalDateTime.MIN);

        LocalDateTime date2 = likeRepository.findByLikerIdAndLikedUserId(userId2, userId1)
                .map(Like::getCreatedAt)
                .orElse(LocalDateTime.MIN);

        return date1.isAfter(date2) ? date1 : date2;
    }
}
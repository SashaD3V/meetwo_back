package com.meetwo.dto.mapper;

import com.meetwo.dto.request.CreatePhotoRequest;
import com.meetwo.dto.request.UpdatePhotoRequest;
import com.meetwo.dto.response.PhotoResponse;
import com.meetwo.entity.Photo;
import com.meetwo.entity.User;
import org.springframework.stereotype.Component;

@Component
public class PhotoMapper {

    public Photo toEntity(CreatePhotoRequest request, User user) {
        Photo photo = new Photo();
        photo.setUser(user);
        photo.setUrl(request.getUrl());
        photo.setPosition(request.getPosition());
        photo.setEstPrincipale(request.getEstPrincipale());
        photo.setAltText(request.getAltText());
        photo.setFileSize(request.getFileSize());
        photo.setWidth(request.getWidth());
        photo.setHeight(request.getHeight());
        photo.setContentType(request.getContentType());
        return photo;
    }

    public PhotoResponse toResponse(Photo photo) {
        PhotoResponse response = new PhotoResponse();
        response.setId(photo.getId());
        response.setUserId(photo.getUser().getId());
        response.setUrl(photo.getUrl());
        response.setPosition(photo.getPosition());
        response.setEstPrincipale(photo.getEstPrincipale());
        response.setAltText(photo.getAltText());
        response.setFileSize(photo.getFileSize());
        response.setWidth(photo.getWidth());
        response.setHeight(photo.getHeight());
        response.setContentType(photo.getContentType());
        response.setCreatedAt(photo.getCreatedAt());
        response.setUpdatedAt(photo.getUpdatedAt());
        return response;
    }

    public void updateEntity(Photo photo, UpdatePhotoRequest request) {
        if (request.getUrl() != null) {
            photo.setUrl(request.getUrl());
        }
        if (request.getPosition() != null) {
            photo.setPosition(request.getPosition());
        }
        if (request.getEstPrincipale() != null) {
            photo.setEstPrincipale(request.getEstPrincipale());
        }
        if (request.getAltText() != null) {
            photo.setAltText(request.getAltText());
        }
    }
}
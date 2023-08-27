package com.pja.bloodcount.mapper;

import com.pja.bloodcount.dto.request.EmailChangeRequest;
import com.pja.bloodcount.dto.response.UserResponse;
import com.pja.bloodcount.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Mapper Class to Map DTO -> Model,
 *                     Model -> DTO
 */
@Component
public class UserMapper {

    public static UserResponse mapToResponseDTO(User user){
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .groupNumber(user.getGroup().getGroupNumber())
                .isActive(user.isActive())
                .build();
    }

    /**
     * Map incoming user details to existing user entity
     * @param request user request dto
     * @param id existing user entity id
     * @return User object instance
     */
    public static User mapToUserModel(EmailChangeRequest request, UUID id){
        return User.builder()
                .id(id)
                .email(request.getEmail())
                .build();
    }

    public static List<UserResponse> mapToResponseListDTO(List<User> users){
        return users.stream()
                .map(user -> UserResponse
                        .builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .groupNumber(user.getGroup().getGroupNumber())
                        .isActive(user.isActive())
                .build())
                .toList();
    }
}

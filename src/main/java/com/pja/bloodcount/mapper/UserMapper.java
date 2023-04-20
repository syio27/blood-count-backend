package com.pja.bloodcount.mapper;

import com.pja.bloodcount.dto.request.UserRequest;
import com.pja.bloodcount.dto.response.UserResponse;
import com.pja.bloodcount.model.User;
import com.pja.bloodcount.model.enums.Role;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserMapper {

    public UserResponse mapToResponseDTO(User user){
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }

    //Used for put method (update existing user entity)
    public User mapToUserModel(UserRequest request, UUID id){
        return User.builder()
                .id(id)
                .name(request.getName())
                .password(request.getPassword())
                .email(request.getEmail())
                .role(Role.STUDENT)
                .build();
    }

    public List<UserResponse> mapToResponseListDTO(List<User> users){

        return users.stream()
                .map(user -> UserResponse
                        .builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .password(user.getPassword())
                .build())
                .toList();
    }
}

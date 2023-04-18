package com.pja.bloodcount.dto.response;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse{
    private UUID id;
    private String name;
    private String password;
    private String email;
}

package com.pja.bloodcount.dto.response;

import com.pja.bloodcount.model.enums.Role;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse{
    private UUID id;
    private String email;
    private Role role;
    private String groupNumber;
}

package com.pja.bloodcount.dto.request;

import com.pja.bloodcount.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InviteUserRequest {

    private UUID inviterUserId;
    private String email;
    private Role role;
    private String groupNumber;
}

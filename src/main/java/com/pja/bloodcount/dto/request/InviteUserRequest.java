package com.pja.bloodcount.dto.request;

import com.pja.bloodcount.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InviteUserRequest {

    private String email;
    private Role role;
}

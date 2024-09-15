package com.pja.bloodcount.service.contract;

import com.pja.bloodcount.dto.request.InviteUserRequest;
import com.pja.bloodcount.dto.response.UserResponse;

import java.util.UUID;

public interface AdminService {

    void invite(InviteUserRequest inviteRequest);
    UserResponse toggleBanUser(UUID id);
}

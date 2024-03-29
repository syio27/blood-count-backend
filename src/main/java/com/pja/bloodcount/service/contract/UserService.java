package com.pja.bloodcount.service.contract;

import com.pja.bloodcount.dto.request.*;
import com.pja.bloodcount.dto.response.AuthenticationResponse;
import com.pja.bloodcount.dto.response.SimpleGameResponse;
import com.pja.bloodcount.dto.response.UserResponse;
import com.pja.bloodcount.model.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse getUserById(UUID id);
    UserResponse getUserByEmail(String email);
    List<UserResponse> getUsersByRole(Role role);
    void delete(UUID id);
    UserResponse update(UUID id, EmailChangeRequest emailChangeRequest);
    Page<UserResponse> getUsers(Pageable pageable);
    AuthenticationResponse changePassword(UUID id, PasswordChangeDTO passwordChangeDTO);
    void assignUserToGroup(UUID id, UserGroupAssignmentRequest request);
    void assignGroupToUsers(UserGroupBatchAssignmentRequest request);
    List<UserResponse> getGroupParticipants(String groupNumber);
    SimpleGameResponse getUserGameById(UUID userId, Long gameId);
    public void forgotPassword(ForgotPasswordRequest request);
    public void resetPassword(ResetPasswordRequest request);
}

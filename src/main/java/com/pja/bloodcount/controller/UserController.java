package com.pja.bloodcount.controller;

import com.pja.bloodcount.dto.request.*;
import com.pja.bloodcount.dto.response.AuthenticationResponse;
import com.pja.bloodcount.dto.response.SimpleGameResponse;
import com.pja.bloodcount.dto.response.UserResponse;
import com.pja.bloodcount.dto.response.UserSelectedAnswerResponse;
import com.pja.bloodcount.exceptions.UserNotAllowedException;
import com.pja.bloodcount.model.Token;
import com.pja.bloodcount.model.User;
import com.pja.bloodcount.model.enums.Role;
import com.pja.bloodcount.service.AdminService;
import com.pja.bloodcount.service.GameService;
import com.pja.bloodcount.service.contract.UserService;
import com.pja.bloodcount.utils.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService service;
    private final AdminService adminService;
    private final GameService gameService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('ROOT') or hasRole('SUPERVISOR')")
    @GetMapping(params = "role")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getAllUsers(@RequestParam("role") Role role){
        return service.getUsersByRole(role);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id){
        return ResponseEntity.ok(service.getUserById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email){
        return ResponseEntity.ok(service.getUserByEmail(email));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROOT')")
    public ResponseEntity<Void> deleteUserById(@PathVariable UUID id){
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<AuthenticationResponse> updatePassword(@PathVariable UUID id,
                                                                 @RequestBody PasswordChangeDTO passwordChangeDTO,
                                                                 Authentication authentication){
        AuthenticationUtil.isRequestFromSameUser(authentication, id);
        return ResponseEntity.ok(service.changePassword(id, passwordChangeDTO));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ROOT')")
    @PostMapping("/invite")
    public ResponseEntity<Void> invite(@RequestBody InviteUserRequest inviteRequest, Authentication authentication) {
        AuthenticationUtil.isRequestFromSameUser(authentication, inviteRequest.getInviterUserId());
        adminService.invite(inviteRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ROOT') or hasRole('SUPERVISOR')")
    @PostMapping("{id}/group")
    public ResponseEntity<Void> assignUserToGroup(@PathVariable UUID id,
                                                  @RequestBody UserGroupAssignmentRequest request,
                                                  Authentication authentication){
        AuthenticationUtil.isRequestFromSameUser(authentication, id);
        service.assignUserToGroup(id, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ROOT') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    @PostMapping("/group")
    public ResponseEntity<Void> assignGroupToUsers(@RequestBody UserGroupBatchAssignmentRequest request){
        service.assignGroupToUsers(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ROOT') or hasRole('SUPERVISOR')")
    @PutMapping("/user/group")
    public ResponseEntity<Void> assignUserToAnotherGroup(@RequestBody UserToNewGroupAssignRequest request){
        service.assignUserToGroup(
                request.getId(),
                UserGroupAssignmentRequest
                        .builder()
                        .groupNumber(request.getGroupNumber())
                        .build());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ROOT') or hasRole('SUPERVISOR')")
    @GetMapping(value = "/group", params = "groupNumber")
    public ResponseEntity<List<UserResponse>> getGroupParticipants(@RequestParam String groupNumber){
        return ResponseEntity.ok(service.getGroupParticipants(groupNumber));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ROOT') or hasRole('SUPERVISOR')")
    @GetMapping(value = "/{userId}/games")
    public ResponseEntity<List<SimpleGameResponse>> getAllCompletedGamesOfStudent(@PathVariable UUID userId){
        return ResponseEntity.ok(gameService.getAllCompletedGamesOfUser(userId));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ROOT') or hasRole('SUPERVISOR') or hasRole('STUDENT')")
    @GetMapping(value = "/{userId}/games/completed")
    public ResponseEntity<List<SimpleGameResponse>> getCompletedGames(@PathVariable UUID userId,
                                                                      Authentication authentication){
        AuthenticationUtil.isRequestFromSameUser(authentication, userId);
        return ResponseEntity.ok(gameService.getAllCompletedGamesOfUser(userId));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ROOT') or hasRole('SUPERVISOR') or hasRole('STUDENT')")
    @GetMapping(value = "/{userId}/games/{gameId}")
    public ResponseEntity<List<UserSelectedAnswerResponse>> getSelectedAnswersOfStudent(@PathVariable UUID userId,
                                                                                          @PathVariable Long gameId){
        return ResponseEntity.ok(gameService.getSelectedAnswersOfGame(userId, gameId));
    }

    @PreAuthorize("hasRole('ROOT')")
    @PostMapping("/{userId}/ban")
    public ResponseEntity<UserResponse> ban(@PathVariable UUID userId, Authentication authentication){
        AuthenticationUtil.isRequestFromSameUser(authentication, userId);
        return ResponseEntity.ok(adminService.banUser(userId));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ROOT') or hasRole('SUPERVISOR') or hasRole('STUDENT')")
    @GetMapping(value = "/{userId}", params = "gameId")
    public ResponseEntity<SimpleGameResponse> getUsersCompletedGameById(@PathVariable UUID userId, @RequestParam Long gameId){
        return ResponseEntity.ok(service.getUserGameById(userId, gameId));
    }
}
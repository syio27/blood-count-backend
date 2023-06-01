package com.pja.bloodcount.controller;

import com.pja.bloodcount.dto.request.InviteUserRequest;
import com.pja.bloodcount.dto.request.PasswordChangeDTO;
import com.pja.bloodcount.dto.request.EmailChangeRequest;
import com.pja.bloodcount.dto.response.AuthenticationResponse;
import com.pja.bloodcount.dto.response.UserResponse;
import com.pja.bloodcount.exceptions.RoleAccessException;
import com.pja.bloodcount.exceptions.UserNotAllowedException;
import com.pja.bloodcount.model.User;
import com.pja.bloodcount.model.enums.Role;
import com.pja.bloodcount.service.AdminService;
import com.pja.bloodcount.service.contract.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

    @PreAuthorize("hasRole('ADMIN')")
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

    @PutMapping("/{id}/email")
    public ResponseEntity<UserResponse> updateEmail(@PathVariable UUID id,
                                            @RequestBody EmailChangeRequest emailChangeRequest,
                                            Authentication authentication){
        log.info("Email from request -> {}", emailChangeRequest.getEmail());
        User userDetails = (User) authentication.getPrincipal();
        log.info("request coming from user with email-> {}", userDetails.getEmail());
        if(!userDetails.getId().equals(id)){
            throw new UserNotAllowedException(
                    "Access restricted, user with email: " +
                    userDetails.getEmail() +
                    " not allowed to this url");
        }
        return ResponseEntity.ok(service.update(id, emailChangeRequest));
    }

    @GetMapping("page-query")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<UserResponse>> pageQuery(Pageable pageable){
        Page<UserResponse> userPage = service.getUsers(pageable);
        List<UserResponse> userResponseList = userPage.stream().toList();
        return ResponseEntity.ok(new PageImpl<>(userResponseList, pageable, userPage.getTotalElements()).toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable UUID id,
                                               Authentication authentication){
        User userDetails = (User) authentication.getPrincipal();
        log.info("request coming from user with email-> {}", userDetails.getEmail());
        if(!userDetails.getId().equals(id)){
            throw new UserNotAllowedException(
                    "Access restricted, user with email: " +
                            userDetails.getEmail() +
                            " not allowed to this url");
        }
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("{id}/password")
    public ResponseEntity<AuthenticationResponse> updatePassword(@PathVariable UUID id,
                                                                 @RequestBody PasswordChangeDTO passwordChangeDTO,
                                                                 Authentication authentication){
        User userDetails = (User) authentication.getPrincipal();
        log.info("request coming from user with email-> {}", userDetails.getEmail());
        if(!userDetails.getId().equals(id)){
            throw new UserNotAllowedException(
                    "Access restricted, user with email: " +
                            userDetails.getEmail() +
                            " not allowed to this url");
        }
        return ResponseEntity.ok(service.changePassword(id, passwordChangeDTO));
    }

    @GetMapping("/current")
    public ResponseEntity<UserDetails> getCurrentUser(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return new ResponseEntity<>(userDetails, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ROOT')")
    @PostMapping("/invite")
    public ResponseEntity<Void> invite(@RequestBody InviteUserRequest inviteRequest, Authentication authentication) {
        adminService.invite(inviteRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

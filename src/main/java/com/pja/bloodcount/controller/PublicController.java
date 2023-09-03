package com.pja.bloodcount.controller;

import com.pja.bloodcount.dto.request.ForgotPasswordRequest;
import com.pja.bloodcount.dto.request.ResetPasswordRequest;
import com.pja.bloodcount.dto.response.GroupResponse;
import com.pja.bloodcount.model.enums.GroupType;
import com.pja.bloodcount.service.contract.GroupService;
import com.pja.bloodcount.service.contract.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("public/api/v1")
@RequiredArgsConstructor
@Slf4j
public class PublicController {

    private final GroupService service;
    private final UserService userService;

    @GetMapping("/groups")
    @ResponseStatus(HttpStatus.OK)
    public List<GroupResponse> getAllGroupsPublic(){
        return service.getPublicGroups();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        userService.forgotPassword(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

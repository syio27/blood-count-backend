package com.pja.bloodcount.controller;

import com.pja.bloodcount.dto.response.GroupResponse;
import com.pja.bloodcount.model.enums.GroupType;
import com.pja.bloodcount.service.contract.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("public/api/v1")
@RequiredArgsConstructor
@Slf4j
public class PublicController {

    private final GroupService service;

    @GetMapping("/groups")
    @ResponseStatus(HttpStatus.OK)
    public List<GroupResponse> getAllGroupsPublic(){
        return service.getPublicGroups();
    }
}

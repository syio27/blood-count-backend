package com.pja.bloodcount.controller;

import com.pja.bloodcount.dto.request.GroupRequest;
import com.pja.bloodcount.dto.response.GroupResponse;
import com.pja.bloodcount.service.contract.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@Slf4j
public class GroupController {

    private final GroupService service;

    @PreAuthorize("hasRole('ROOT') or hasRole('ADMIN') or hasRole('STUDENT') or hasRole('SUPERVISOR')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<GroupResponse> getAllGroups(){
        return service.getAllGroups();
    }

    @PreAuthorize("hasRole('ROOT') or hasRole('ADMIN') or hasRole('STUDENT') or hasRole('SUPERVISOR')")
    @GetMapping("/{groupNumber}")
    @ResponseStatus(HttpStatus.OK)
    public GroupResponse getGroupByNumber(@PathVariable String groupNumber){
        return service.getGroupByNumber(groupNumber);
    }

    @PreAuthorize("hasRole('ROOT') or hasRole('ADMIN') or hasRole('STUDENT') or hasRole('SUPERVISOR')")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public GroupResponse createGroup(@RequestBody GroupRequest request){
        return service.createGroup(request);
    }
}

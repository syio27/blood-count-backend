package com.pja.bloodcount.controller;

import com.pja.bloodcount.dto.request.GroupRequest;
import com.pja.bloodcount.dto.response.GroupResponse;
import com.pja.bloodcount.model.enums.GroupType;
import com.pja.bloodcount.service.contract.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @PreAuthorize("hasRole('ROOT') or hasRole('ADMIN') or hasRole('SUPERVISOR')")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public GroupResponse createGroup(@RequestBody GroupRequest request){
        return service.createGroup(request);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/student-groups")
    public ResponseEntity<List<GroupResponse>> getAllStudentGroups(){
        return ResponseEntity.ok(service.getAllGroupsByType(GroupType.STUDENT_GROUP));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ROOT')")
    @GetMapping("/admin-groups")
    public ResponseEntity<List<GroupResponse>> getAllAdministrationGroups(){
        return ResponseEntity.ok(service.getAllGroupsByType(GroupType.ADMIN_GROUP));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ROOT')")
    @PostMapping(value = "/clear", params = "groupNumber")
    public ResponseEntity<Void> clearGroupFromUsers(@RequestParam String groupNumber){
        service.clearGroupFromUsers(groupNumber);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ROOT')")
    @DeleteMapping(value = "/{groupNumber}")
    public ResponseEntity<Void> deleteGroup(@PathVariable String groupNumber){
        service.deleteGroup(groupNumber);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('ROOT')")
    @PostMapping(value = "/{groupNumber}/users/{userId}")
    public ResponseEntity<Void> deleteUserFromGroup(@PathVariable String groupNumber, @PathVariable UUID userId){
        service.deleteUserFromGroup(groupNumber, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

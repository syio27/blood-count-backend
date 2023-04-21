package com.pja.bloodcount.controller;

import com.pja.bloodcount.dto.request.UserRequest;
import com.pja.bloodcount.dto.response.UserResponse;
import com.pja.bloodcount.service.contract.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class UserController {

    private final UserService service;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getAllUsers(){
        return service.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id){
        return ResponseEntity.ok(service.getUserById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email){
        return ResponseEntity.ok(service.getUserByEmail(email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserById(@PathVariable UUID id,
                                               @Valid @RequestBody UserRequest userRequest,
                                               BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        service.update(id, userRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("page-query")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> pageQuery(Pageable pageable){
        Page<UserResponse> userPage = service.getUsers(pageable);
        List<UserResponse> userResponseList = userPage.stream().toList();
        return new PageImpl<>(userResponseList, pageable, userPage.getTotalElements()).toList();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable UUID id){
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

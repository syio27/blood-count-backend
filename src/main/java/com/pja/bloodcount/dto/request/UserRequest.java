package com.pja.bloodcount.dto.request;

import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    @Column(nullable = false)
    private String name;
    private String password;
    @Email
    @Column(unique = true, nullable = false)
    private String email;
}

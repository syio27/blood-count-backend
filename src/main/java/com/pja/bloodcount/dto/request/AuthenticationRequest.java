package com.pja.bloodcount.dto.request;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest{
    private String email;
    private String password;
}

package com.pja.bloodcount.dto.response;

import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse{
    private String token;
    private Date expirationDate;
}

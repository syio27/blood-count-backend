package com.pja.bloodcount.service.contract;

import com.pja.bloodcount.dto.request.RegisterRequest;
import com.pja.bloodcount.dto.request.AuthenticationRequest;
import com.pja.bloodcount.dto.response.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest registerRequest);

    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);
}

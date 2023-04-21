package com.pja.bloodcount.service.auth;

import com.pja.bloodcount.dto.request.AuthenticationRequest;
import com.pja.bloodcount.dto.request.RegisterRequest;
import com.pja.bloodcount.dto.response.AuthenticationResponse;
import com.pja.bloodcount.exceptions.InvalidCredentialsException;
import com.pja.bloodcount.exceptions.ResourceConflictException;
import com.pja.bloodcount.exceptions.UserNotFoundException;
import com.pja.bloodcount.exceptions.UserWithEmailNotFoundException;
import com.pja.bloodcount.service.contract.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.pja.bloodcount.repository.UserRepository;
import com.pja.bloodcount.model.User;
import com.pja.bloodcount.model.enums.Role;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse register(RegisterRequest registerRequest) {
        if(userRepository.findUserByEmail(registerRequest.getEmail()).isPresent()){
            throw new ResourceConflictException(registerRequest.getEmail());
        }

        User user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.STUDENT)
                .build();

        userRepository.save(user);
        log.info("User {} {} is registered", user.getId(), user.getEmail());
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()
            ));
        }
        catch (BadCredentialsException ex){
            throw new InvalidCredentialsException("Email or password is not correct, please try again");
        }

        User user = userRepository
                .findUserByEmail(authenticationRequest.getEmail())
                .orElseThrow(() -> new UserWithEmailNotFoundException(authenticationRequest.getEmail()));

        log.info("User {} {} is authenticated", user.getId(), user.getEmail());
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }
}

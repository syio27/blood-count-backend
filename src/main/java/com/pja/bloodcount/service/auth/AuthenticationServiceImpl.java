package com.pja.bloodcount.service.auth;

import com.pja.bloodcount.dto.request.AuthenticationRequest;
import com.pja.bloodcount.dto.request.RegisterRequest;
import com.pja.bloodcount.dto.response.AuthenticationResponse;
import com.pja.bloodcount.exceptions.*;
import com.pja.bloodcount.model.Group;
import com.pja.bloodcount.service.contract.AuthenticationService;
import com.pja.bloodcount.utils.CredentialValidationUtil;
import com.pja.bloodcount.validation.GroupValidator;
import com.pja.bloodcount.validation.UserValidator;
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
    private final GroupValidator groupValidator;
    private final UserValidator userValidator;

    @Override
    public AuthenticationResponse register(RegisterRequest registerRequest) {
        if (!CredentialValidationUtil.validateEmail(registerRequest.getEmail())) {
            throw new EmailValidationException("Email is not valid, doesnt match regex rule");
        }

        if (userRepository.findUserByEmail(registerRequest.getEmail()).isPresent()) {
            throw new UserConflictException(registerRequest.getEmail());
        }

        if (!CredentialValidationUtil.validatePassword(registerRequest.getPassword())) {
            throw new PasswordValidationException("Password is not valid, doesnt match regex rule");
        }

        Group group = groupValidator.validateIfExistsAndGet(registerRequest.getGroupNumber());

        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.STUDENT)
                .isActive(true)
                .build();

        group.addUser(user);

        userRepository.save(user);
        log.info("User {} {} is registered", user.getId(), user.getEmail());
        var jwtToken = jwtService.generateToken(user, registerRequest.getTimezoneOffset());
        var jwtExpirationDate = jwtService.extractExpiration(jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .expirationDate(jwtExpirationDate)
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        User user = userValidator.validateEmailAndGet(authenticationRequest.getEmail());
        if(!user.isActive()){
            throw new UserBannedException("User is banned");
        }
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()
            ));
        }
        catch (BadCredentialsException ex){
            throw new InvalidCredentialsException("Email or password is not correct, please try again");
        }

        log.info("User {} {} is authenticated", user.getId(), user.getEmail());
        var jwtToken = jwtService.generateToken(user, authenticationRequest.getTimezoneOffset());
        var jwtExpirationDate = jwtService.extractExpiration(jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .expirationDate(jwtExpirationDate)
                .build();
    }
}

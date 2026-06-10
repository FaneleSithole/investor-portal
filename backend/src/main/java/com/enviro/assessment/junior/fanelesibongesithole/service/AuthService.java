package com.enviro.assessment.junior.fanelesibongesithole.service;

import com.enviro.assessment.junior.fanelesibongesithole.dto.AuthResponse;
import com.enviro.assessment.junior.fanelesibongesithole.dto.LoginRequest;
import com.enviro.assessment.junior.fanelesibongesithole.dto.RegisterRequest;
import com.enviro.assessment.junior.fanelesibongesithole.dto.UserProfileDto;
import com.enviro.assessment.junior.fanelesibongesithole.entity.UserEntity;
import com.enviro.assessment.junior.fanelesibongesithole.exception.ApiException;
import com.enviro.assessment.junior.fanelesibongesithole.repository.UserRepository;
import com.enviro.assessment.junior.fanelesibongesithole.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final int RETIREMENT_MIN_AGE = 65;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ApiException("Email is already registered", HttpStatus.CONFLICT);
        }

        userRepository.save(new UserEntity(
                request.email().trim().toLowerCase(),
                passwordEncoder.encode(request.password()),
                request.firstName().trim(),
                request.lastName().trim(),
                request.firmName() != null ? request.firmName().trim() : null,
                request.dateOfBirth()
        ));

        return login(new LoginRequest(request.email(), request.password()));
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email().trim().toLowerCase(),
                            request.password()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
            UserEntity user = ((UserPrincipal) auth.getPrincipal()).getUser();
            return new AuthResponse("Login successful", toProfile(user));
        } catch (BadCredentialsException e) {
            throw new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }
    }

    public UserProfileDto currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ApiException("Authentication required", HttpStatus.UNAUTHORIZED);
        }
        return toProfile(principal.getUser());
    }

    public UserProfileDto toProfile(UserEntity user) {
        int age = user.getAge();
        return new UserProfileDto(
                user.getFirstName(),
                user.getLastName(),
                user.getFirmName() != null ? user.getFirmName() : "Fanele & Partners",
                user.getRole(),
                age,
                age > RETIREMENT_MIN_AGE
        );
    }
}

package com.leonardosoares.fintrack_api.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.leonardosoares.fintrack_api.controller.dto.auth.AuthResponseDTO;
import com.leonardosoares.fintrack_api.controller.dto.auth.LoginRequestDTO;
import com.leonardosoares.fintrack_api.controller.dto.auth.RegisterRequestDTO;
import com.leonardosoares.fintrack_api.model.User;
import com.leonardosoares.fintrack_api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDTO register(RegisterRequestDTO request) {
        var user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        
        return new AuthResponseDTO(jwtToken);
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        var user = userRepository.findByEmail(request.email())
                .orElseThrow();

        var jwtToken = jwtService.generateToken(user);

        return new AuthResponseDTO(jwtToken);
    }
}
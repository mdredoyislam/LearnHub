package com.redoy.FirstSpringBoot.service;

import com.redoy.FirstSpringBoot.dto.AuthResponse;
import com.redoy.FirstSpringBoot.dto.LoginRequest;
import com.redoy.FirstSpringBoot.dto.RegisterRequest;
import com.redoy.FirstSpringBoot.dto.UserProfileDto;
import com.redoy.FirstSpringBoot.entity.UserAccount;
import com.redoy.FirstSpringBoot.exception.ResourceNotFoundException;
import com.redoy.FirstSpringBoot.repository.UserAccountRepository;
import com.redoy.FirstSpringBoot.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userAccountRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }
        UserAccount user = UserAccount.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(UserAccount.Role.STUDENT)
                .status(true)
                .build();
        userAccountRepository.save(user);
        String token = jwtUtil.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .userId(user.getUserAccountId())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        UserAccount user = userAccountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String token = jwtUtil.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .userId(user.getUserAccountId())
                .build();
    }

    public UserProfileDto getProfile(String email) {
        UserAccount user = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToProfileDto(user);
    }

    public void changePassword(String email, String oldPassword, String newPassword) {
        UserAccount user = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userAccountRepository.save(user);
    }

    private UserProfileDto mapToProfileDto(UserAccount user) {
        return UserProfileDto.builder()
                .userAccountId(user.getUserAccountId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profilePhoto(user.getProfilePhoto())
                .status(user.isStatus())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

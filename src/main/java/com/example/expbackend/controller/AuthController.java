package com.example.expbackend.controller;

import com.example.expbackend.dto.*;
import com.example.expbackend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Authentication Controller
 * Handles user registration, login, and profile endpoints
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for user authentication")
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user
     *
     * @param request registration details (name, email, password)
     * @return JWT token and user info
     *
     * Example:
     * POST /api/auth/register
     * Body: {
     *   "name": "Poojitha",
     *   "email": "poojitha@gmail.com",
     *   "password": "123456"
     * }
     * Response: {
     *   "success": true,
     *   "data": {
     *     "token": "jwt_token",
     *     "id": 1,
     *     "name": "Poojitha",
     *     "email": "poojitha@gmail.com"
     *   }
     * }
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid registration data or email already exists")
    })
    public ResponseEntity<com.example.expbackend.dto.ApiResponse<AuthResponse>> register(
        @RequestBody RegisterRequest request
    ) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(com.example.expbackend.dto.ApiResponse.success(response, "User registered successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(com.example.expbackend.dto.ApiResponse.error("REGISTRATION_ERROR", e.getMessage()));
        }
    }

    /**
     * Login user
     *
     * @param request login credentials (email, password)
     * @return JWT token and user info
     *
     * Example:
     * POST /api/auth/login
     * Body: {
     *   "email": "poojitha@gmail.com",
     *   "password": "123456"
     * }
     * Response: {
     *   "success": true,
     *   "data": {
     *     "token": "jwt_token",
     *     "id": 1,
     *     "name": "Poojitha",
     *     "email": "poojitha@gmail.com"
     *   }
     * }
     */
    @PostMapping("/login")
    @Operation(summary = "Login user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "400", description = "Invalid credentials")
    })
    public ResponseEntity<com.example.expbackend.dto.ApiResponse<AuthResponse>> login(
        @RequestBody LoginRequest request
    ) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(com.example.expbackend.dto.ApiResponse.success(response, "Login successful"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(com.example.expbackend.dto.ApiResponse.error("LOGIN_ERROR", e.getMessage()));
        }
    }

    /**
     * Get current user profile
     *
     * @param request HTTP request (extracts userId from JWT token)
     * @return user profile details
     *
     * Example:
     * GET /api/auth/me
     * Authorization: Bearer jwt_token
     * Response: {
     *   "success": true,
     *   "data": {
     *     "id": 1,
     *     "name": "Poojitha",
     *     "email": "poojitha@gmail.com",
     *     "createdAt": 1680518400000
     *   }
     * }
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    @ApiResponse(responseCode = "200", description = "Profile retrieved successfully")
    public ResponseEntity<com.example.expbackend.dto.ApiResponse<UserProfileResponse>> getProfile(
        HttpServletRequest request
    ) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(com.example.expbackend.dto.ApiResponse.error("UNAUTHORIZED", "User not authenticated"));
            }

            UserProfileResponse profile = authService.getProfile(userId);
            return ResponseEntity.ok(com.example.expbackend.dto.ApiResponse.success(profile, "Profile retrieved successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(com.example.expbackend.dto.ApiResponse.error("PROFILE_ERROR", e.getMessage()));
        }
    }
}

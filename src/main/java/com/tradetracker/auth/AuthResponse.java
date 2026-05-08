package com.tradetracker.auth;

public record AuthResponse(
        Long userId,
        String email,
        String role,
        String businessName,
        String message
) {
}

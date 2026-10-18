package com.project.techstore.user.entity;

import java.util.Locale;

/**
 * Nguồn xác thực của tài khoản người dùng.
 */
public enum AuthProvider {
    LOCAL,
    GOOGLE;

    public static AuthProvider fromRegistrationId(String registrationId) {
        if (registrationId == null) {
            throw new IllegalArgumentException("OAuth registration id không được để trống");
        }

        return switch (registrationId.toLowerCase(Locale.ROOT)) {
            case "google" -> GOOGLE;
            default -> throw new IllegalArgumentException(
                    "OAuth provider chưa được hỗ trợ: " + registrationId
            );
        };
    }
}

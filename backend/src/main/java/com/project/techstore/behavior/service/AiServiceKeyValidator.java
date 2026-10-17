package com.project.techstore.behavior.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class AiServiceKeyValidator {

    private final byte[] configuredKey;

    public AiServiceKeyValidator(@Value("${ai.internal-api-key:}") String configuredKey) {
        this.configuredKey = configuredKey.getBytes(StandardCharsets.UTF_8);
    }

    public boolean matches(String providedKey) {
        if (configuredKey.length == 0 || providedKey == null || providedKey.isBlank()) {
            return false;
        }
        return MessageDigest.isEqual(configuredKey, providedKey.getBytes(StandardCharsets.UTF_8));
    }
}

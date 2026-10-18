package com.project.techstore.auth.oauth2;

import com.project.techstore.common.exception.AppException;
import com.project.techstore.common.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lưu mã trao đổi OAuth trong thời gian ngắn.
 *
 * <p>Mã chỉ dùng một lần và chỉ chứa user id. Với môi trường nhiều instance,
 * service này nên được thay bằng Redis hoặc một kho lưu trữ dùng chung.</p>
 */
@Service
public class OAuthLoginCodeService {

    private static final Duration CODE_TTL = Duration.ofMinutes(2);

    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, PendingLogin> pendingLogins = new ConcurrentHashMap<>();

    public String issue(Long userId) {
        cleanupExpiredCodes();

        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        String code = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        pendingLogins.put(code, new PendingLogin(userId, Instant.now().plus(CODE_TTL)));
        return code;
    }

    public Long consume(String code) {
        if (!StringUtils.hasText(code)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "OAuth code không hợp lệ");
        }

        PendingLogin pendingLogin = pendingLogins.remove(code);
        if (pendingLogin == null || pendingLogin.expiresAt().isBefore(Instant.now())) {
            throw new AppException(
                    ErrorCode.INVALID_REQUEST,
                    "OAuth code không hợp lệ hoặc đã hết hạn"
            );
        }

        return pendingLogin.userId();
    }

    private void cleanupExpiredCodes() {
        Instant now = Instant.now();
        pendingLogins.entrySet().removeIf(entry -> entry.getValue().expiresAt().isBefore(now));
    }

    private record PendingLogin(Long userId, Instant expiresAt) {
    }
}

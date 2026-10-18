package com.project.techstore.auth;

import com.project.techstore.auth.oauth2.OAuthLoginCodeService;
import com.project.techstore.common.exception.AppException;
import com.project.techstore.common.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OAuthLoginCodeServiceTest {

    @Test
    void issueAndConsumeCodeOnce() {
        OAuthLoginCodeService service = new OAuthLoginCodeService();

        String code = service.issue(42L);

        assertEquals(42L, service.consume(code));
        AppException exception = assertThrows(AppException.class, () -> service.consume(code));
        assertEquals(ErrorCode.INVALID_REQUEST, exception.getErrorCode());
    }

    @Test
    void rejectBlankCode() {
        OAuthLoginCodeService service = new OAuthLoginCodeService();

        AppException exception = assertThrows(AppException.class, () -> service.consume(" "));

        assertEquals(ErrorCode.INVALID_REQUEST, exception.getErrorCode());
    }
}

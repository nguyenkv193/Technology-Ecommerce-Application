package com.project.techstore.auth.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    @Value("${oauth2.google.frontend-success-url:http://localhost:5173/oauth2/callback}")
    private String frontendSuccessUrl;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        log.warn("OAuth provider trả về lỗi: {}", exception.getMessage());

        String redirectUrl = UriComponentsBuilder
                .fromUriString(frontendSuccessUrl)
                .replaceQueryParam("error", "OAUTH_LOGIN_FAILED")
                .replaceQueryParam("code")
                .build()
                .encode()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }
}

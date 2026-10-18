package com.project.techstore.auth.oauth2;

import com.project.techstore.auth.service.AuthService;
import com.project.techstore.common.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final OAuthLoginCodeService oauthLoginCodeService;

    @Value("${oauth2.google.frontend-success-url:http://localhost:5173/oauth2/callback}")
    private String frontendSuccessUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        try {
            if (!(authentication instanceof OAuth2AuthenticationToken oauthAuthentication)) {
                throw new IllegalStateException("OAuth authentication không hợp lệ");
            }

            OAuth2User oauthUser = oauthAuthentication.getPrincipal();
            String registrationId = oauthAuthentication.getAuthorizedClientRegistrationId();
            String subject = oauthUser.getAttribute("sub");
            String email = oauthUser.getAttribute("email");
            String fullName = oauthUser.getAttribute("name");
            boolean emailVerified = isEmailVerified(oauthUser.getAttribute("email_verified"));

            var user = authService.loginWithOAuth2(
                    registrationId,
                    subject,
                    email,
                    fullName,
                    emailVerified
            );

            String code = oauthLoginCodeService.issue(user.getId());
            String redirectUrl = UriComponentsBuilder
                    .fromUriString(frontendSuccessUrl)
                    .replaceQueryParam("code", code)
                    .replaceQueryParam("error")
                    .build()
                    .encode()
                    .toUriString();

            response.sendRedirect(redirectUrl);
        } catch (AppException ex) {
            log.warn("OAuth login bị từ chối: {}", ex.getMessage());
            redirectWithError(response, ex.getErrorCode().name());
        } catch (Exception ex) {
            log.error("OAuth login thất bại", ex);
            redirectWithError(response, "OAUTH_LOGIN_FAILED");
        }
    }

    private boolean isEmailVerified(Object value) {
        return Boolean.TRUE.equals(value)
                || (value instanceof String stringValue && Boolean.parseBoolean(stringValue));
    }

    private void redirectWithError(HttpServletResponse response, String errorCode) throws IOException {
        String redirectUrl = UriComponentsBuilder
                .fromUriString(frontendSuccessUrl)
                .replaceQueryParam("error", errorCode)
                .replaceQueryParam("code")
                .build()
                .encode()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }
}

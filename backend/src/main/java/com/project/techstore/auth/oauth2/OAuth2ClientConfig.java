package com.project.techstore.auth.oauth2;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

/**
 * Đăng ký Google OAuth client khi feature được bật.
 *
 * <p>Giữ feature tắt mặc định để backend vẫn khởi động được khi local
 * chưa có credential Google.</p>
 */
@Configuration
@ConditionalOnProperty(prefix = "oauth2.google", name = "enabled", havingValue = "true")
public class OAuth2ClientConfig {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(
            @Value("${oauth2.google.client-id}") String clientId,
            @Value("${oauth2.google.client-secret}") String clientSecret,
            @Value("${oauth2.google.redirect-uri}") String redirectUri
    ) {
        ClientRegistration google = CommonOAuth2Provider.GOOGLE
                .getBuilder("google")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .scope("openid", "profile", "email")
                .redirectUri(redirectUri)
                .build();

        return new InMemoryClientRegistrationRepository(google);
    }
}

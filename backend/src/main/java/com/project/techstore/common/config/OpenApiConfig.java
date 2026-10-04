package com.project.techstore.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình Swagger / OpenAPI 3 tài liệu hóa REST API kèm xác thực Bearer JWT.
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TechStore E-Commerce & AI Recommendation API")
                        .version("1.0.0")
                        .description("Hệ thống API Thương mại điện tử chuyên về sản phẩm công nghệ tích hợp AI phân tích hành vi và gợi ý sản phẩm.")
                        .contact(new Contact()
                                .name("Graduation Thesis Development Team")
                                .email("contact@techstore.project.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://spring.io/")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Nhập Access Token JWT theo định dạng: Bearer {token}")));
    }
}

package com.ssafy.backend.common.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * SwaggerConfig는 Swagger UI + JWT 인증 설정을 담당하는 클래스입니다.
 */
@Configuration
public class SwaggerAPIConfig {

    @Bean
    public OpenAPI openAPI() {

        // Swagger에서 JWT 인증 버튼을 생성하기 위한 설정
        SecurityScheme jwtScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("JWT");

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("JWT", jwtScheme)
                )
                .addSecurityItem(securityRequirement)

                .info(swaggerApiInfo())

                .servers(List.of(
                        new Server().url("http://localhost:8080").description("local")
                ))

                .externalDocs(new ExternalDocumentation()
                        .description("GetchaBE Repo")
                        .url("https://github.com/SideProjectSFY/getchaBE.git")
                );
    }

    private Info swaggerApiInfo() {
        return new Info()
                .title("GetchaBE REST API")
                .version("v1.0.0")
                .description("<h3>애니메이션 굿즈 경매 플랫폼 REST API 문서</h3>");
    }
}

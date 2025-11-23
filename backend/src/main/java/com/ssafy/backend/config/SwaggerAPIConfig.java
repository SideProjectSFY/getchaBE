package com.ssafy.backend.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * SwaggerConfig는 Swagger UI 설정을 담당하는 클래스입니다.
 */

@Configuration
public class SwaggerAPIConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .servers(List.of(
                        // 로컬 환경 시 open API 문서
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

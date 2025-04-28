package com.productmanagement.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@OpenAPIDefinition(
    servers = {
        @Server(url = "https://uhyun.shop", description = "Server"),
        @Server(url = "http://localhost:8080", description = "Local")
    }
)
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Components components = new Components()
            .addSecuritySchemes(HttpHeaders.AUTHORIZATION, new SecurityScheme()
                .name(HttpHeaders.AUTHORIZATION)
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .bearerFormat("JWT"));

        return new OpenAPI()
            .info(new Info().title("상품 및 옵션 관리 API").version("v1"))
            .addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION))
            .components(components);
    }

}
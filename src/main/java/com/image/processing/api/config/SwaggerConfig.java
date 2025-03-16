package com.image.processing.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Image Compression Application")
                        .version("1.0")
                        .description("API Documentation for Image Compression application"))
                .addServersItem(new Server().url("http://localhost:8080"))
                .addServersItem(new Server().url("http://localhost:8080"));
    }
}
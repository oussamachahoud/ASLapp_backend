package com.example.aslapp_backend.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ASL App API")
                        .description("REST API for the ASL e-commerce application — authentication, users, products, cart and orders.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("ASL App Team")
                                .email("support@aslapp.com")))
                .addSecurityItem(new SecurityRequirement().addList("JWT Cookie"))
                .components(new Components()
                        .addSecuritySchemes("Access JWT Cookie",
                                new SecurityScheme()
                                        .name("access_token")
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.COOKIE)
                                        .description("JWT token stored in an HttpOnly cookie named \"access_token\"")));
    }
}

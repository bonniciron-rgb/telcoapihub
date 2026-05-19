package com.telcoapihub.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI hubOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Telco Network API Hub")
                        .version("0.1.0")
                        .description("CAMARA / GSMA Open Gateway network API hub: number verification, "
                                + "KYC match, age verification and SIM swap, aggregated across operators "
                                + "and aggregators with per-query metered billing."))
                .components(new Components().addSecuritySchemes("ApiKey",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-API-Key")))
                .addSecurityItem(new SecurityRequirement().addList("ApiKey"));
    }
}

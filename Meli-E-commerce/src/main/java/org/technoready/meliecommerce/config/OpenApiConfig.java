package org.technoready.meliecommerce.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration class for Swagger documentation.
 * Configures API metadata and documentation settings.
 * DATE: 22 - October - 2025
 *
 * @author Jorge Armando Avila Carrillo | NAOID:3310
 * @version 1.3.0
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MeliECommerce API")
                        .version("1.3.0")
                        .description("REST API for MeliECommerce order management system")
                        .contact(new Contact()
                                .name("ckacy01")
                                .email("it.jorgeavila@gmail.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development server")
                ));
    }
}
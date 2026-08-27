package com.brilliantmule.servicedesk.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for OpenAPI documentation metadata.
 * <p>
 * Customizes the API description exposed by springdoc-openapi at
 * {@code /v3/api-docs} and in Swagger UI at {@code /swagger-ui.html}.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Provides the top-level OpenAPI definition for the Service Desk API.
     * <p>
     * Sets the document title, description, and version shown in generated
     * API documentation.
     *
     * @return the configured OpenAPI definition
     */
    @Bean
    public OpenAPI serviceDeskOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Brilliant Mule Service Desk API")
                        .description(
                                "REST API for managing Brilliant Mule service desk incidents."
                        )
                        .version("1.0.0")
                        .license(new License()
                                .name("Apache License 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0"))
                        .contact(new Contact()
                                .name("Alan Belisle")
                                .email("abelisle@salesforce.com")));
    }
}

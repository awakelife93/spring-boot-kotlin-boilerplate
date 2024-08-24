package com.example.demo.common.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(
  info = Info(
    title = "Spring Boot Boilerplate",
    description = "Spring Boot Boilerplate API Docs",
    version = "v1"
  )
)
@Configuration
class SwaggerConfig {
  @Bean
  fun openAPI(): OpenAPI {
    val securityAPIKeyScheme = SecurityScheme()
      .type(SecurityScheme.Type.APIKEY)
      .`in`(SecurityScheme.In.HEADER)
      .name("X-API-KEY")

    val securityJWTScheme = SecurityScheme()
      .type(SecurityScheme.Type.HTTP)
      .scheme("bearer")
      .bearerFormat("JWT")
      .`in`(SecurityScheme.In.HEADER)
      .name("Authorization")

    val securityRequirement = SecurityRequirement()
      .addList("API Key")
      .addList("Bearer Token")

    return OpenAPI()
      .components(
        Components()
          .addSecuritySchemes("API Key", securityAPIKeyScheme)
          .addSecuritySchemes("Bearer Token", securityJWTScheme)
      )
      .security(mutableListOf(securityRequirement))
  }

  @Bean
  fun v1OpenApi(): GroupedOpenApi {
    return GroupedOpenApi
      .builder()
      .group("V1 API")
      .pathsToMatch("/api/v1/**/**")
      .build()
  }
}

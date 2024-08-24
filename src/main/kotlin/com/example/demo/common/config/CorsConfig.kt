package com.example.demo.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfig {
  @Bean
  fun corsConfigurationSource(): CorsConfigurationSource {
    val configuration = CorsConfiguration()
    val source = UrlBasedCorsConfigurationSource()

    configuration.allowedOrigins = mutableListOf("*")

    configuration.allowedMethods =
      mutableListOf("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH")

    configuration.allowedHeaders =
      mutableListOf("Authorization", "Cache-Control", "Content-Type")

    configuration.allowCredentials = true

    source.registerCorsConfiguration("/**", configuration)
    
    return source
  }
}

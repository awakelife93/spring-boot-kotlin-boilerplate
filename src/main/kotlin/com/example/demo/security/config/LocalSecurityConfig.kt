package com.example.demo.security.config

import com.example.demo.security.component.CustomAuthenticationEntryPoint
import com.example.demo.security.component.provider.AuthProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.annotation.web.configurers.*
import org.springframework.security.web.SecurityFilterChain

@Profile("local")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
class LocalSecurityConfig(
  private val authProvider: AuthProvider,
  private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint
) {

  @Bean
  fun webSecurityCustomizer(): WebSecurityCustomizer {
    return WebSecurityCustomizer { webSecurity: WebSecurity ->
      webSecurity
        .ignoring()
        .requestMatchers(*authProvider.ignoreListDefaultEndpoints())
    }
  }

  @Bean
  @Throws(Exception::class)
  fun authenticationManager(
    authenticationConfiguration: AuthenticationConfiguration
  ): AuthenticationManager {
    return authenticationConfiguration.authenticationManager
  }

  @Bean
  @Throws(Exception::class)
  fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
    return authProvider
      .defaultSecurityFilterChain(httpSecurity)
      .exceptionHandling { exceptionHandling: ExceptionHandlingConfigurer<HttpSecurity?> ->
        exceptionHandling.authenticationEntryPoint(
          customAuthenticationEntryPoint
        )
      }
      .build()
  }
}

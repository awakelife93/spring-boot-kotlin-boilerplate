package com.example.demo.security.config

import com.example.demo.common.config.CorsConfig
import com.example.demo.security.component.CustomAuthenticationEntryPoint
import com.example.demo.security.component.filter.APIKeyAuthFilter
import com.example.demo.security.component.filter.JWTAuthFilter
import com.example.demo.security.component.provider.AuthProvider
import com.example.demo.security.component.provider.JWTProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.annotation.web.configurers.*
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
class WebSecurityConfig(
  private val corsConfig: CorsConfig,
  private val authProvider: AuthProvider,
  private val jwtProvider: JWTProvider,
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
  fun filterChain(http: HttpSecurity): SecurityFilterChain {
    return http
      .csrf { csrf: CsrfConfigurer<HttpSecurity?> -> csrf.disable() }
      .httpBasic { httpBasic: HttpBasicConfigurer<HttpSecurity?> -> httpBasic.disable() }
      .formLogin { formLogin: FormLoginConfigurer<HttpSecurity?> -> formLogin.disable() }
      .cors { cors: CorsConfigurer<HttpSecurity?> ->
        cors.configurationSource(
          corsConfig.corsConfigurationSource()
        )
      }
      .authorizeHttpRequests { request ->
        request
          .requestMatchers(*authProvider.whiteListDefaultEndpoints())
          .permitAll()
          .anyRequest()
          .authenticated()
      }
      .sessionManagement { httpSecuritySessionManagementConfigurer: SessionManagementConfigurer<HttpSecurity?> ->
        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(
          SessionCreationPolicy.STATELESS
        )
      }
      .addFilterBefore(
        JWTAuthFilter(jwtProvider),
        UsernamePasswordAuthenticationFilter::class.java
      )
      .addFilterAfter(
        APIKeyAuthFilter(authProvider),
        UsernamePasswordAuthenticationFilter::class.java
      )
      .exceptionHandling { exceptionHandling: ExceptionHandlingConfigurer<HttpSecurity?> ->
        exceptionHandling.authenticationEntryPoint(
          customAuthenticationEntryPoint
        )
      }
      .build()
  }
}

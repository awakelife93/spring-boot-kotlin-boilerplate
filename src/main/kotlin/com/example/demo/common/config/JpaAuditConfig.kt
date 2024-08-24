package com.example.demo.common.config

import com.example.demo.security.service.impl.AuditorAwareImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@Configuration
class JpaAuditConfig {
  @Bean
  fun auditorProvider(): AuditorAware<Long> = AuditorAwareImpl()
}

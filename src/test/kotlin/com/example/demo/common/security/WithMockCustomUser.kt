package com.example.demo.common.security

import org.springframework.security.test.context.support.WithSecurityContext

@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory::class)
annotation class WithMockCustomUser(
  val id: String = "1",
  val email: String = "awakelife93@gmail.com",
  val name: String = "awakelife93",
  val role: String = "USER"
)

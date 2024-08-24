package com.example.demo.common.security

import com.example.demo.common.BaseIntegrationControllerItem
import com.example.demo.security.component.provider.JWTProvider
import org.springframework.boot.test.mock.mockito.MockBean

open class SecurityItem : BaseIntegrationControllerItem() {
  @MockBean
  protected lateinit var jwtProvider: JWTProvider
}

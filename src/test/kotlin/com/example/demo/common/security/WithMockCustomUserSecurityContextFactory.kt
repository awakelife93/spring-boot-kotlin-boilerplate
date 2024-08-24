package com.example.demo.common.security

import com.example.demo.security.SecurityUserItem
import com.example.demo.security.UserAdapter
import com.example.demo.user.constant.UserRole
import com.example.demo.user.entity.User
import org.instancio.Instancio
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory

class WithMockCustomUserSecurityContextFactory
  : WithSecurityContextFactory<WithMockCustomUser> {

  override fun createSecurityContext(annotation: WithMockCustomUser): SecurityContext {
    val securityContext = SecurityContextHolder.createEmptyContext()
    val user = Instancio.create(User::class.java)
    val securityUserItem = SecurityUserItem.of(user.also {
      it.id = annotation.id.toLong()
      it.email = annotation.email
      it.name = annotation.name
      it.role = UserRole.valueOf(annotation.role)
    })

    val userAdapter = UserAdapter(securityUserItem)

    val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
      userAdapter,
      null,
      userAdapter.authorities
    )

    securityContext.authentication = usernamePasswordAuthenticationToken
    return securityContext
  }
}

package com.example.demo.security.service.impl

import com.example.demo.security.UserAdapter
import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*

class AuditorAwareImpl : AuditorAware<Long> {
  override fun getCurrentAuditor(): Optional<Long> {
    val authentication = SecurityContextHolder.getContext().authentication

    return when {
      authentication == null ||
        !authentication.isAuthenticated ||
        authentication.principal == "anonymousUser" -> Optional.empty()

      else -> {
        val userAdapter = authentication.principal as UserAdapter
        Optional.of(userAdapter.securityUserItem.userId)
      }
    }
  }
}

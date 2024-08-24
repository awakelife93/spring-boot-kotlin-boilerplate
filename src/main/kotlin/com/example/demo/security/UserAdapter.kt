package com.example.demo.security

import com.example.demo.user.constant.UserRole
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User

class UserAdapter(val securityUserItem: SecurityUserItem) : User(
  securityUserItem.email,
  "",
  authorities(securityUserItem.role)
) {
  companion object {
    private fun authorities(
      role: UserRole
    ): Collection<GrantedAuthority> {
      val authorities: MutableCollection<GrantedAuthority> = ArrayList()
      authorities.add(SimpleGrantedAuthority("ROLE_$role"))

      return authorities
    }
  }
}

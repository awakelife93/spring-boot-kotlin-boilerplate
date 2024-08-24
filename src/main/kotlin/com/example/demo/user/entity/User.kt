package com.example.demo.user.entity

import com.example.demo.common.entity.BaseSoftDeleteEntity
import com.example.demo.post.entity.Post
import com.example.demo.user.constant.UserRole
import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Entity
@Table(name = "\"user\"")
@AttributeOverride(name = "id", column = Column(name = "user_id"))
@SQLDelete(sql = "UPDATE \"user\" SET deleted_dt = CURRENT_TIMESTAMP WHERE user_id = ?")
@SQLRestriction("deleted_dt IS NULL")
data class User(
  @Column(nullable = false)
  var name: String,

  @Column(
    unique = true,
    nullable = false,
    updatable = false
  )
  var email: String,

  @Column(nullable = false)
  var password: String,

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  var role: UserRole = UserRole.USER,

  @OneToMany(cascade = [CascadeType.ALL], mappedBy = "user", orphanRemoval = true)
  var posts: List<Post> = ArrayList()
) : BaseSoftDeleteEntity() {

  fun update(name: String, role: UserRole): User {
    this.name = name
    this.role = role
    return this
  }

  fun encodePassword(bCryptPasswordEncoder: BCryptPasswordEncoder): User {
    this.password = bCryptPasswordEncoder.encode(this.password)
    return this
  }

  fun validatePassword(
    password: String?,
    bCryptPasswordEncoder: BCryptPasswordEncoder
  ): Boolean {
    return bCryptPasswordEncoder.matches(password, this.password)
  }
}

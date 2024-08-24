package com.example.demo.common.entity

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(unique = true, updatable = false, nullable = false)
  var id: Long = 0L,

  @CreatedDate
  @Column(nullable = false, updatable = false)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  var createdDt: LocalDateTime = LocalDateTime.now(),

  @LastModifiedDate
  @Column(nullable = false)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  var updatedDt: LocalDateTime? = null
)

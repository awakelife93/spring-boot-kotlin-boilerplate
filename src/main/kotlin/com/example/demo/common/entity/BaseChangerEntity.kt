package com.example.demo.common.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseChangerEntity(
  @CreatedBy
  @Column(nullable = false, updatable = false)
  var createdBy: Long? = null,

  @LastModifiedBy
  @Column(nullable = false)
  var updatedBy: Long? = null
) : BaseEntity()

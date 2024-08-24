package com.example.demo.common.annotaction

import com.example.demo.utils.EnumValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [EnumValidator::class])
@Target(
  AnnotationTarget.FUNCTION,
  AnnotationTarget.PROPERTY_GETTER,
  AnnotationTarget.PROPERTY_SETTER,
  AnnotationTarget.FIELD,
  AnnotationTarget.VALUE_PARAMETER
)
@Retention(
  AnnotationRetention.RUNTIME
)
annotation class ValidEnum(
  val message: String = "Invalid value.",
  val groups: Array<KClass<*>> = [],
  val payload: Array<KClass<out Payload>> = [],
  val enumClass: KClass<out Enum<*>>
)

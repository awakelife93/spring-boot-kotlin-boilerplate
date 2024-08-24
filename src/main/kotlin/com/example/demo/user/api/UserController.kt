package com.example.demo.user.api

import com.example.demo.common.response.ErrorResponse
import com.example.demo.security.SecurityUserItem
import com.example.demo.security.annotation.CurrentUser
import com.example.demo.user.application.ChangeUserService
import com.example.demo.user.application.GetUserService
import com.example.demo.user.dto.serve.request.CreateUserRequest
import com.example.demo.user.dto.serve.request.UpdateUserRequest
import com.example.demo.user.dto.serve.response.CreateUserResponse
import com.example.demo.user.dto.serve.response.GetUserResponse
import com.example.demo.user.dto.serve.response.UpdateMeResponse
import com.example.demo.user.dto.serve.response.UpdateUserResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "User", description = "User API")
@RestController
@RequestMapping("/api/v1/users")
class UserController(
  private val getUserService: GetUserService,
  private val changeUserService: ChangeUserService
) {

  @Operation(operationId = "getUserById", summary = "Get User", description = "Get User By User Id API")
  @ApiResponses(
    value = [ApiResponse(
      responseCode = "200",
      description = "OK",
      content = arrayOf(Content(schema = Schema(implementation = GetUserResponse::class)))
    ), ApiResponse(
      responseCode = "401",
      description = "Full authentication is required to access this resource",
      content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class)))
    ), ApiResponse(
      responseCode = "404",
      description = "User Not Found userId = {userId} or email = {email}",
      content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class)))
    )]
  )
  @GetMapping("/{userId}")
  fun getUserById(
    @PathVariable("userId", required = true) userId: Long
  ): ResponseEntity<GetUserResponse> = ResponseEntity.ok(getUserService.getUserById(userId))

  @Operation(operationId = "getUserList", summary = "Get User List", description = "Get User List API")
  @ApiResponses(
    value = [ApiResponse(
      responseCode = "200",
      description = "OK",
      content = arrayOf(Content(array = ArraySchema(schema = Schema(implementation = GetUserResponse::class))))
    )]
  )
  @GetMapping
  fun getUserList(pageable: Pageable): ResponseEntity<Page<GetUserResponse>> = ResponseEntity.ok(
    getUserService.getUserList(
      pageable
    )
  )

  @Operation(operationId = "createUser", summary = "Create User", description = "Create User API")
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "201",
        description = "Created",
        content = arrayOf(Content(schema = Schema(implementation = CreateUserResponse::class)))
      ),
      ApiResponse(
        responseCode = "400",
        description = "Field Valid Error",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class)))
      ),
      ApiResponse(
        responseCode = "409",
        description = "Already User Exist userId = {userId} or email = {email}",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class)))
      )]
  )
  @PostMapping("/register")
  fun createUser(
    @RequestBody @Valid createUserRequest: CreateUserRequest
  ): ResponseEntity<CreateUserResponse> = ResponseEntity.status(HttpStatus.CREATED).body(
    changeUserService.createUser(
      createUserRequest
    )
  )

  @Operation(operationId = "updateUser", summary = "Update User", description = "Update User API")
  @ApiResponses(
    value = [ApiResponse(
      responseCode = "200",
      description = "OK",
      content = arrayOf(Content(schema = Schema(implementation = UpdateUserResponse::class)))
    ), ApiResponse(
      responseCode = "400",
      description = "Request Body Valid Error",
      content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class)))
    ), ApiResponse(
      responseCode = "401",
      description = "Full authentication is required to access this resource",
      content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class)))
    ), ApiResponse(
      responseCode = "404",
      description = "User Not Found userId = {userId} or email = {email}",
      content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class)))
    )]
  )
  @PatchMapping("/{userId}")
  fun updateUser(
    @RequestBody @Valid updateUserRequest: UpdateUserRequest,
    @PathVariable("userId", required = true) userId: Long
  ): ResponseEntity<UpdateUserResponse> = ResponseEntity.ok(
    changeUserService.updateUser(
      userId,
      updateUserRequest
    )
  )

  @Operation(operationId = "updateMe", summary = "Update Me", description = "Update Me API")
  @ApiResponses(
    value = [ApiResponse(
      responseCode = "200",
      description = "OK",
      content = arrayOf(Content(schema = Schema(implementation = UpdateMeResponse::class)))
    ), ApiResponse(
      responseCode = "400",
      description = "Request Body Valid Error",
      content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class)))
    ), ApiResponse(
      responseCode = "401",
      description = "Full authentication is required to access this resource",
      content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class)))
    ), ApiResponse(
      responseCode = "404",
      description = "User Not Found userId = {userId} or email = {email}",
      content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class)))
    )]
  )
  @PatchMapping
  fun updateMe(
    @RequestBody @Valid updateUserRequest: UpdateUserRequest,
    @CurrentUser securityUserItem: SecurityUserItem
  ): ResponseEntity<UpdateMeResponse> =
    ResponseEntity.ok(
      changeUserService.updateMe(
        securityUserItem.userId,
        updateUserRequest
      )
    )

  @Operation(operationId = "deleteUser", summary = "Delete User", description = "Delete User API")
  @ApiResponses(
    value = [ApiResponse(
      responseCode = "204",
      description = "No Content"
    ), ApiResponse(
      responseCode = "401",
      description = "Full authentication is required to access this resource",
      content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class)))
    )]
  )
  @DeleteMapping("/{userId}")
  fun deleteUser(@PathVariable("userId", required = true) userId: Long): ResponseEntity<Void> {
    changeUserService.deleteUser(userId)

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
  }
}

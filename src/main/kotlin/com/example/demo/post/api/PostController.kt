package com.example.demo.post.api

import com.example.demo.common.response.ErrorResponse
import com.example.demo.post.application.ChangePostService
import com.example.demo.post.application.GetPostService
import com.example.demo.post.dto.serve.request.CreatePostRequest
import com.example.demo.post.dto.serve.request.GetExcludeUsersPostsRequest
import com.example.demo.post.dto.serve.request.UpdatePostRequest
import com.example.demo.post.dto.serve.response.CreatePostResponse
import com.example.demo.post.dto.serve.response.GetPostResponse
import com.example.demo.post.dto.serve.response.UpdatePostResponse
import com.example.demo.security.SecurityUserItem
import com.example.demo.security.annotation.CurrentUser
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

@Tag(name = "Post", description = "Post API")
@RestController
@RequestMapping("/api/v1/posts")
class PostController(
  private val getPostService: GetPostService,
  private val changePostService: ChangePostService
) {

  @Operation(operationId = "createPost", summary = "Create Post", description = "Create Post API")
  @ApiResponses(
    value = [ApiResponse(
      responseCode = "201",
      description = "Create Post",
      content = arrayOf(Content(schema = Schema(implementation = CreatePostResponse::class)))
    ), ApiResponse(
      responseCode = "400",
      description = "Request Body Valid Error",
      content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class)))
    ), ApiResponse(
      responseCode = "401",
      description = "Full authentication is required to access this resource",
      content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class)))
    )]
  )
  @PutMapping
  fun createPost(
    @RequestBody @Valid createPostRequest: CreatePostRequest,
    @CurrentUser securityUserItem: SecurityUserItem
  ): ResponseEntity<CreatePostResponse> =
    ResponseEntity.status(HttpStatus.CREATED).body(
      changePostService.createPost(
        securityUserItem.userId,
        createPostRequest,
      )
    )

  @Operation(operationId = "getPostList", summary = "Get Post List", description = "Get Post List API")
  @ApiResponses(
    value = [ApiResponse(
      responseCode = "200",
      description = "OK",
      content = arrayOf(Content(array = ArraySchema(schema = Schema(implementation = GetPostResponse::class))))
    )]
  )
  @GetMapping
  fun getPostList(pageable: Pageable): ResponseEntity<Page<GetPostResponse>> = ResponseEntity.ok(
    getPostService.getPostList(
      pageable
    )
  )

  @Operation(
    operationId = "getExcludeUsersPosts",
    summary = "Get Exclude Users By Post List",
    description = "Get Exclude Users By Post List API"
  )
  @ApiResponses(
    value = [ApiResponse(
      responseCode = "200",
      description = "OK",
      content = arrayOf(Content(array = ArraySchema(schema = Schema(implementation = GetPostResponse::class))))
    ), ApiResponse(
      responseCode = "400",
      description = "Request Body Valid Error",
      content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class)))
    ), ApiResponse(
      responseCode = "401",
      description = "Full authentication is required to access this resource",
      content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class)))
    )]
  )
  @GetMapping("/exclude-users")
  fun getExcludeUsersPostList(
    getExcludeUsersPostsRequest: GetExcludeUsersPostsRequest,
    pageable: Pageable
  ): ResponseEntity<Page<GetPostResponse>> =
    ResponseEntity.ok(
      getPostService.getExcludeUsersPostList(
        getExcludeUsersPostsRequest,
        pageable
      )
    )

  @Operation(operationId = "getPostById", summary = "Get Post", description = "Get Post By Post Id API")
  @ApiResponses(
    value = [ApiResponse(
      responseCode = "200",
      description = "OK",
      content = arrayOf(Content(schema = Schema(implementation = GetPostResponse::class)))
    ), ApiResponse(
      responseCode = "401",
      description = "Full authentication is required to access this resource",
      content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class)))
    ), ApiResponse(
      responseCode = "404",
      description = "Post Not Found postId = {postId}",
      content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class)))
    )]
  )
  @GetMapping("/{postId}")
  fun getPostById(
    @PathVariable("postId", required = true) postId: Long
  ): ResponseEntity<GetPostResponse> = ResponseEntity.ok(getPostService.getPostById(postId))

  @Operation(operationId = "updatePost", summary = "Update Post", description = "Update Post API")
  @ApiResponses(
    value = [ApiResponse(
      responseCode = "200",
      description = "OK",
      content = arrayOf(Content(schema = Schema(implementation = UpdatePostResponse::class)))
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
      description = "Post Not Found postId = {postId}",
      content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class)))
    )]
  )
  @PatchMapping("/{postId}")
  fun updatePost(
    @RequestBody @Valid updatePostRequest: UpdatePostRequest,
    @PathVariable("postId", required = true) postId: Long
  ): ResponseEntity<UpdatePostResponse> = ResponseEntity.ok(
    changePostService.updatePost(
      postId,
      updatePostRequest
    )
  )

  @Operation(operationId = "deletePost", summary = "Delete Post", description = "Delete Post API")
  @ApiResponses(
    value = [ApiResponse(
      responseCode = "204",
      description = "Delete Post"
    ), ApiResponse(
      responseCode = "401",
      description = "Full authentication is required to access this resource",
      content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class)))
    )]
  )
  @DeleteMapping("/{postId}")
  fun deletePost(@PathVariable("postId", required = true) postId: Long): ResponseEntity<Void> {
    changePostService.deletePost(postId)

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
  }
}

package com.blog.blogapi.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * DTOs for authentication and authorization.
 */
data class LoginRequest(
    @field:NotBlank(message = "username is required")
    var username: String = "",
    
    @field:NotBlank(message = "password is required")
    @field:Size(min = 6, message = "password must be at least 6 characters")
    var password: String = ""
)

data class RegisterRequest(
    @field:NotBlank(message = "username is required")
    @field:Size(min = 3, max = 50, message = "username must be between 3 and 50 characters")
    var username: String = "",
    
    @field:NotBlank(message = "email is required")
    @field:Email(message = "please provide a valid email address")
    var email: String = "",
    
    @field:NotBlank(message = "password is required")
    @field:Size(min = 6, message = "password must be at least 6 characters")
    var password: String = "",
    
    @field:NotBlank(message = "display name is required")
    var displayName: String = ""
)

data class TokenResponse(
    val tokenType: String = "Bearer",
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)

data class UserProfileResponse(
    val id: Long,
    val username: String,
    val email: String,
    val displayName: String,
    val avatarUrl: String?,
    val bio: String?,
    val role: String,
    val createdAt: String,
    val updatedAt: String?
)

data class ChangePasswordRequest(
    @field:NotBlank(message = "current password is required")
    var currentPassword: String = "",
    
    @field:NotBlank(message = "new password is required")
    @field:Size(min = 6, message = "new password must be at least 6 characters")
    var newPassword: String = ""
)
package com.blog.blogapi.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * User entity for authentication and user management.
 */
@Entity
@Table(
    name = "users",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["username"]),
        UniqueConstraint(columnNames = ["email"])
    ]
)
data class User(
    @field:NotBlank(message = "Username is required")
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(name = "username", nullable = false, unique = true, length = 50)
    var username: String = "",
    
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Please provide a valid email address")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    var email: String = "",
    
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 6, max = 100, message = "Password must be at least 6 characters")
    @Column(name = "password", nullable = false, length = 100)
    var password: String = "",
    
    @field:NotBlank(message = "Display name is required")
    @field:Size(max = 100, message = "Display name is too long")
    @Column(name = "display_name", nullable = false, length = 100)
    var displayName: String = "",
    
    @Column(name = "avatar_url", length = 255)
    var avatarUrl: String? = null,
    
    @Column(name = "bio", length = 500)
    var bio: String? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    var role: UserRole = UserRole.USER,
    
    @Column(name = "is_enabled", nullable = false)
    var isEnabled: Boolean = true
) : BaseEntity() {
    
    fun updateFrom(other: User) {
        this.displayName = other.displayName
        this.avatarUrl = other.avatarUrl
        this.bio = other.bio
        this.role = other.role
        this.isEnabled = other.isEnabled
    }
}

/**
 * User role enum
 */
enum class UserRole {
    GUEST,    // 仅可浏览
    USER,     // 可评论
    AUTHOR,   // 可发文
    EDITOR,   // 可管理他人文章
    ADMIN,    // 可管理所有内容
    ROOT      // 超级管理员
}
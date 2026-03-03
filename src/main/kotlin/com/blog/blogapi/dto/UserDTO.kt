package com.blog.blogapi.dto

import com.blog.blogapi.entity.UserRole

/**
 * DTO 对象用于用户相关的数据传输
 */
data class UserCreateRequest(
    val username: String = "",      // 用户名
    val email: String = "",         // 邮箱地址
    val password: String = "",      // 密码
    val displayName: String = "",   // 显示名称
    val role: UserRole = UserRole.USER,  // 用户角色
    val isEnabled: Boolean = true   // 是否启用账户
)

data class UserUpdateRequest(
    val displayName: String = "",   // 显示名称
    val avatarUrl: String? = null,  // 头像URL，可选
    val bio: String? = null,        // 个人简介，可选
    val role: UserRole = UserRole.USER,  // 用户角色
    val isEnabled: Boolean = true   // 是否启用账户
)

data class UserResponse(
    val id: Long,                   // 用户ID
    val username: String,           // 用户名
    val email: String,              // 邮箱地址
    val displayName: String,        // 显示名称
    val avatarUrl: String?,         // 头像URL
    val bio: String?,               // 个人简介
    val role: String,               // 用户角色（字符串表示）
    val isEnabled: Boolean,         // 账户是否启用
    val articleCount: Int,          // 文章数量
    val commentCount: Int,          // 评论数量
    val createdAt: String,          // 创建时间
    val updatedAt: String?          // 更新时间
)

data class UserSimpleResponse(
    val id: Long,                   // 用户ID
    val username: String,           // 用户名
    val displayName: String,        // 显示名称
    val avatarUrl: String?          // 头像URL
)

data class UserProfileUpdateRequest(
    val displayName: String = "",   // 显示名称
    val avatarUrl: String? = null,  // 头像URL
    val bio: String? = null         // 个人简介
)

data class UserStatsResponse(
    val totalUsers: Long,           // 总用户数
    val activeUsers: Long,          // 活跃用户数
    val newUsersToday: Long,        // 今日新增用户
    val newUsersThisWeek: Long      // 本周新增用户
)
package com.blog.blogapi.service

import com.blog.blogapi.dto.*
import com.blog.blogapi.entity.UserRole
import com.blog.blogapi.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserService(
    private val userRepository: UserRepository
) {
    
    /**
     * 获取用户列表（分页）
     */
    fun getAllUsers(pageable: Pageable): Page<UserResponse> {
        return userRepository.findAllByIsDeletedFalse(pageable).map { user ->
            convertToUserResponse(user)
        }
    }
    
    /**
     * 获取用户详情
     */
    fun getUserById(id: Long): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("用户不存在") }
        
        if (user.isDeleted) {
            throw IllegalArgumentException("用户已被删除")
        }
        
        return convertToUserResponse(user)
    }
    
    /**
     * 创建用户
     */
    fun createUser(request: UserCreateRequest): UserResponse {
        // 验证用户名和邮箱
        if (userRepository.existsByUsernameAndIsDeletedFalse(request.username)) {
            throw IllegalArgumentException("用户名已存在")
        }
        if (userRepository.existsByEmailAndIsDeletedFalse(request.email)) {
            throw IllegalArgumentException("邮箱已注册")
        }
        
        val user = com.blog.blogapi.entity.User(
            username = request.username,
            email = request.email,
            password = "", // 密码需后续设置
            displayName = request.displayName,
            role = request.role,
            isEnabled = request.isEnabled
        )
        
        val savedUser = userRepository.save(user)
        return convertToUserResponse(savedUser)
    }
    
    /**
     * 更新用户
     */
    fun updateUser(id: Long, request: UserUpdateRequest): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("用户不存在") }
        
        if (user.isDeleted) {
            throw IllegalArgumentException("用户已被删除")
        }
        
        user.displayName = request.displayName
        user.avatarUrl = request.avatarUrl
        user.bio = request.bio
        user.role = request.role
        user.isEnabled = request.isEnabled
        
        val updatedUser = userRepository.save(user)
        return convertToUserResponse(updatedUser)
    }
    
    /**
     * 删除用户（软删除）
     */
    fun deleteUser(id: Long): Boolean {
        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("用户不存在") }
        
        if (user.role == UserRole.ROOT) {
            throw IllegalArgumentException("无法删除超级管理员")
        }
        
        user.isDeleted = true
        userRepository.save(user)
        return true
    }
    
    /**
     * 搜索用户
     */
    fun searchUsers(query: String, pageable: Pageable): Page<UserResponse> {
        return userRepository.findByUsernameContainingIgnoreCaseAndIsDeletedFalse(query, pageable)
            .map { user -> convertToUserResponse(user) }
    }
    
    /**
     * 获取用户统计信息
     */
    fun getUserStats(): UserStatsResponse {
        val users = userRepository.findAllByIsDeletedFalse()
        val enabledUsers = users.count { it.isEnabled }
        
        return UserStatsResponse(
            totalUsers = users.size.toLong(),
            activeUsers = enabledUsers.toLong(),
            newUsersToday = 0L,  // 需要额外统计
            newUsersThisWeek = 0L // 需要额外统计
        )
    }
    
    /**
     * 启用/禁用用户
     */
    fun toggleUserStatus(id: Long, enabled: Boolean): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("用户不存在") }
        
        if (user.isDeleted) {
            throw IllegalArgumentException("用户已被删除")
        }
        
        if (user.role == UserRole.ROOT && !enabled) {
            throw IllegalArgumentException("无法禁用超级管理员")
        }
        
        user.isEnabled = enabled
        val updatedUser = userRepository.save(user)
        return convertToUserResponse(updatedUser)
    }
    
    /**
     * 转换为UserResponse
     */
    private fun convertToUserResponse(user: com.blog.blogapi.entity.User): UserResponse {
        return UserResponse(
            id = user.id,
            username = user.username,
            email = user.email,
            displayName = user.displayName,
            avatarUrl = user.avatarUrl,
            bio = user.bio,
            role = user.role.name,
            isEnabled = user.isEnabled,
            articleCount = 0,  // 待实现
            commentCount = 0,  // 待实现
            createdAt = user.createdAt?.toString() ?: LocalDateTime.now().toString(),
            updatedAt = user.updatedAt?.toString()
        )
    }
}
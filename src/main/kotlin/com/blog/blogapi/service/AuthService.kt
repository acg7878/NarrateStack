package com.blog.blogapi.service

import com.blog.blogapi.dto.*
import com.blog.blogapi.entity.User
import com.blog.blogapi.entity.UserRole
import com.blog.blogapi.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {
    
    /**
     * 用户注册
     */
    fun register(request: RegisterRequest): UserProfileResponse {
        // 检查用户名和邮箱是否已存在
        if (userRepository.existsByUsernameAndIsDeletedFalse(request.username)) {
            throw IllegalArgumentException("用户名已存在")
        }
        if (userRepository.existsByEmailAndIsDeletedFalse(request.email)) {
            throw IllegalArgumentException("邮箱已注册")
        }
        
        // 创建用户
        val user = User(
            username = request.username,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            displayName = request.displayName,
            role = UserRole.USER,
            isEnabled = true
        )
        
        val savedUser = userRepository.save(user)
        return UserProfileResponse(
            id = savedUser.id,
            username = savedUser.username,
            email = savedUser.email,
            displayName = savedUser.displayName,
            avatarUrl = savedUser.avatarUrl,
            bio = savedUser.bio,
            role = savedUser.role.name,
            createdAt = savedUser.createdAt?.toString() ?: LocalDateTime.now().toString(),
            updatedAt = savedUser.updatedAt?.toString()
        )
    }
    
    /**
     * 用户登录
     */
    fun login(request: LoginRequest): TokenResponse {
        val user = userRepository.findByUsernameAndIsDeletedFalse(request.username)
            ?: throw IllegalArgumentException("用户名或密码错误")
        
        if (!user.isEnabled) {
            throw IllegalArgumentException("账号已被禁用")
        }
        
        if (!passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("用户名或密码错误")
        }
        
        val accessToken = jwtService.generateAccessToken(user)
        val refreshToken = jwtService.generateRefreshToken(user)
        
        return TokenResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = 86400000 // 24小时
        )
    }
    
    /**
     * 刷新访问令牌
     */
    fun refreshToken(refreshToken: String): TokenResponse {
        val username = jwtService.extractUsername(refreshToken)
        val user = userRepository.findByUsernameAndIsDeletedFalse(username)
            ?: throw IllegalArgumentException("无效的刷新令牌")
        
        if (!jwtService.validateToken(refreshToken, user)) {
            throw IllegalArgumentException("刷新令牌已过期")
        }
        
        val newAccessToken = jwtService.generateAccessToken(user)
        val newRefreshToken = jwtService.generateRefreshToken(user)
        
        return TokenResponse(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken,
            expiresIn = 86400000
        )
    }
    
    /**
     * 获取用户资料
     */
    fun getUserProfile(username: String): UserProfileResponse {
        val user = userRepository.findByUsernameAndIsDeletedFalse(username)
            ?: throw IllegalArgumentException("用户不存在")
        
        return UserProfileResponse(
            id = user.id,
            username = user.username,
            email = user.email,
            displayName = user.displayName,
            avatarUrl = user.avatarUrl,
            bio = user.bio,
            role = user.role.name,
            createdAt = user.createdAt?.toString() ?: LocalDateTime.now().toString(),
            updatedAt = user.updatedAt?.toString()
        )
    }
    
    /**
     * 修改密码
     */
    fun changePassword(username: String, request: ChangePasswordRequest): Boolean {
        val user = userRepository.findByUsernameAndIsDeletedFalse(username)
            ?: throw IllegalArgumentException("用户不存在")
        
        if (!passwordEncoder.matches(request.currentPassword, user.password)) {
            throw IllegalArgumentException("当前密码错误")
        }
        
        user.password = passwordEncoder.encode(request.newPassword)
        userRepository.save(user)
        return true
    }
}
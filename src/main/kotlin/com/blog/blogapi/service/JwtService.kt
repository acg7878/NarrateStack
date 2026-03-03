package com.blog.blogapi.service

import com.blog.blogapi.entity.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.Key
import java.util.*

@Service
class JwtService {
    
    @Value("\${app.jwt.secret}")
    private lateinit var jwtSecret: String
    
    @Value("\${app.jwt.expiration-ms}")
    private var jwtExpirationMs: Long = 86400000L
    
    /**
     * 生成JWT密钥
     */
    private fun getSigningKey(): Key {
        return Keys.hmacShaKeyFor(jwtSecret.toByteArray())
    }
    
    /**
     * 从令牌中提取用户名
     */
    fun extractUsername(token: String): String {
        return extractAllClaims(token).subject
    }
    
    /**
     * 提取用户ID
     */
    fun extractUserId(token: String): Long {
        return extractAllClaims(token)["user_id", Long::class.java] ?: 0L
    }
    
    /**
     * 提取用户角色
     */
    fun extractUserRole(token: String): String {
        return extractAllClaims(token).get("role", String::class.java) ?: "USER"
    }
    
    /**
     * 检查令牌是否过期
     */
    fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }
    
    /**
     * 验证令牌
     */
    fun validateToken(token: String, user: User): Boolean {
        val username = extractUsername(token)
        return (username == user.username) && !isTokenExpired(token)
    }
    
    /**
     * 生成访问令牌
     */
    fun generateAccessToken(user: User): String {
        return generateToken(
            user = user,
            expirationDate = Date(System.currentTimeMillis() + jwtExpirationMs),
            tokenType = "access"
        )
    }
    
    /**
     * 生成刷新令牌
     */
    fun generateRefreshToken(user: User): String {
        return generateToken(
            user = user,
            expirationDate = Date(System.currentTimeMillis() + jwtExpirationMs * 7), // 7天
            tokenType = "refresh"
        )
    }
    
    /**
     * 通用令牌生成方法
     */
    private fun generateToken(user: User, expirationDate: Date, tokenType: String): String {
        return Jwts.builder()
            .setSubject(user.username)
            .claim("user_id", user.id)
            .claim("role", user.role.name)
            .claim("type", tokenType)
            .setIssuedAt(Date())
            .setExpiration(expirationDate)
            .signWith(getSigningKey())
            .compact()
    }
    
    /**
     * 解析令牌的所有声明
     */
    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }
    
    /**
     * 提取过期时间
     */
    private fun extractExpiration(token: String): Date {
        return extractAllClaims(token).expiration
    }
}
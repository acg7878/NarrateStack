package com.blog.blogapi.repository

import com.blog.blogapi.entity.User
import com.blog.blogapi.entity.UserRole
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : BaseRepository<User> {
    
    fun findByUsernameAndIsDeletedFalse(username: String): User?
    
    fun findByEmailAndIsDeletedFalse(email: String): User?
    
    fun findByUsernameOrEmail(username: String, email: String): User?
    
    fun findByRoleAndIsDeletedFalse(role: UserRole): List<User>
    
    fun findByIsEnabledTrueAndIsDeletedFalse(): List<User>
    
    fun existsByUsernameAndIsDeletedFalse(username: String): Boolean
    
    fun existsByEmailAndIsDeletedFalse(email: String): Boolean
    
    fun findAllByIsDeletedFalse(pageable: Pageable): Page<User>
    
    fun findByUsernameContainingIgnoreCaseAndIsDeletedFalse(
        username: String, pageable: Pageable
    ): Page<User>
}
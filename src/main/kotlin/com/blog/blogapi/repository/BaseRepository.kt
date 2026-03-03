package com.blog.blogapi.repository

import com.blog.blogapi.entity.BaseEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean

/**
 * Base repository interface with common operations.
 */
@NoRepositoryBean
interface BaseRepository<T : BaseEntity> : JpaRepository<T, Long> {
    
    /**
     * Find all non-deleted entities.
     */
    fun findAllByIsDeletedFalse(): List<T>
    
    /**
     * Find paginated non-deleted entities.
     */
    fun findAllByIsDeletedFalse(pageable: Pageable): Page<T>
    
    /**
     * Soft delete by setting isDeleted to true.
     */
    fun softDeleteById(id: Long): Boolean
}
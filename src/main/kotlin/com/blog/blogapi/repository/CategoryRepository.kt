package com.blog.blogapi.repository

import com.blog.blogapi.entity.Category
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : BaseRepository<Category> {
    
    fun findBySlugAndIsDeletedFalse(slug: String): Category?
    
    fun findByNameContainingIgnoreCaseAndIsDeletedFalse(
        name: String, pageable: Pageable
    ): Page<Category>
    
    fun findAllByParentIdIsNullAndIsDeletedFalse(): List<Category>
    
    fun findAllByParentIdAndIsDeletedFalse(parentId: Long): List<Category>
    
    fun findAllByIsDeletedFalseOrderBySortOrderAsc(): List<Category>
    
    fun existsBySlugAndIsDeletedFalse(slug: String): Boolean
    
    fun findBySlugContainingIgnoreCaseAndIsDeletedFalse(
        slug: String, pageable: Pageable
    ): Page<Category>
}
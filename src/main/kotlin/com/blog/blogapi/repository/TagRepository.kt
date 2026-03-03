package com.blog.blogapi.repository

import com.blog.blogapi.entity.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
interface TagRepository : BaseRepository<Tag> {
    
    fun findBySlugAndIsDeletedFalse(slug: String): Tag?
    
    fun findByNameContainingIgnoreCaseAndIsDeletedFalse(
        name: String, pageable: Pageable
    ): Page<Tag>
    
    fun findAllByIsDeletedFalseOrderByArticleCountDesc(): List<Tag>
    
    fun existsBySlugAndIsDeletedFalse(slug: String): Boolean
    
    fun findBySlugContainingIgnoreCaseAndIsDeletedFalse(
        slug: String, pageable: Pageable
    ): Page<Tag>
    
    fun findAllByIsDeletedFalseOrderByNameAsc(): List<Tag>
}
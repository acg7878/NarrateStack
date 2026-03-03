package com.blog.blogapi.repository

import com.blog.blogapi.entity.Article
import com.blog.blogapi.entity.ArticleStatus
import com.blog.blogapi.entity.Category
import com.blog.blogapi.entity.Tag
import com.blog.blogapi.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository : BaseRepository<Article> {
    
    fun findBySlugAndIsDeletedFalse(slug: String): Article?
    
    fun findByStatusAndIsDeletedFalse(status: ArticleStatus): List<Article>
    
    fun findByStatusAndIsDeletedFalse(
        status: ArticleStatus, pageable: Pageable
    ): Page<Article>
    
    fun findByAuthorAndIsDeletedFalse(author: User, pageable: Pageable): Page<Article>
    
    fun findByAuthorIdAndIsDeletedFalse(authorId: Long, pageable: Pageable): Page<Article>
    
    fun findByTitleContainingIgnoreCaseAndIsDeletedFalse(
        title: String, pageable: Pageable
    ): Page<Article>
    
    fun findByCategoryAndIsDeletedFalse(
        category: Category, pageable: Pageable
    ): Page<Article>
    
    fun findByCategoryIdAndIsDeletedFalse(
        categoryId: Long, pageable: Pageable
    ): Page<Article>
    
    fun findByTagsContainingAndIsDeletedFalse(
        tag: Tag, pageable: Pageable
    ): Page<Article>
    
    fun findByStatusAndIsDeletedFalseOrderByPublishedAtDesc(
        status: ArticleStatus, pageable: Pageable
    ): Page<Article>
    
    fun findByStatusAndIsFeaturedTrueAndIsDeletedFalseOrderByPublishedAtDesc(
        status: ArticleStatus, pageable: Pageable
    ): Page<Article>
    
    fun findByStatusAndIsRecommendedTrueAndIsDeletedFalseOrderByPublishedAtDesc(
        status: ArticleStatus, pageable: Pageable
    ): Page<Article>
    
    fun findByStatusAndCategoryIdAndIsDeletedFalseOrderByPublishedAtDesc(
        status: ArticleStatus, categoryId: Long, pageable: Pageable
    ): Page<Article>
    
    fun existsBySlugAndIsDeletedFalse(slug: String): Boolean
    
    fun findTop10ByStatusAndIsDeletedFalseOrderByViewsDesc(status: ArticleStatus): List<Article>
    
    fun findTop10ByStatusAndIsDeletedFalseOrderByLikesDesc(status: ArticleStatus): List<Article>
}
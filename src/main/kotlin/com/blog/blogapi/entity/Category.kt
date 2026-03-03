package com.blog.blogapi.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * Category entity for grouping articles.
 */
@Entity
@Table(
    name = "categories",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["slug"])
    ]
)
data class Category(
    @field:NotBlank(message = "Category name is required")
    @field:Size(max = 100, message = "Category name is too long")
    @Column(name = "name", nullable = false, length = 100)
    var name: String = "",
    
    @field:NotBlank(message = "Slug is required")
    @field:Size(max = 100, message = "Slug is too long")
    @Column(name = "slug", nullable = false, unique = true, length = 100)
    var slug: String = "",
    
    @Column(name = "description", length = 500)
    var description: String? = null,
    
    @Column(name = "icon", length = 50)
    var icon: String? = null,
    
    @Column(name = "color", length = 20)
    var color: String? = null,
    
    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0,
    
    @Column(name = "parent_id")
    var parentId: Long? = null,
    
    @Column(name = "article_count", nullable = false)
    var articleCount: Int = 0
) : BaseEntity() {
    
    fun incrementArticleCount() {
        this.articleCount++
    }
    
    fun decrementArticleCount() {
        if (this.articleCount > 0) {
            this.articleCount--
        }
    }
}
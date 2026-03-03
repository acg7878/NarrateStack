package com.blog.blogapi.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * Tag entity for labeling articles.
 */
@Entity
@Table(
    name = "tags",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["slug"])
    ]
)
data class Tag(
    @field:NotBlank(message = "Tag name is required")
    @field:Size(max = 50, message = "Tag name is too long")
    @Column(name = "name", nullable = false, length = 50)
    var name: String = "",
    
    @field:NotBlank(message = "Slug is required")
    @field:Size(max = 50, message = "Slug is too long")
    @Column(name = "slug", nullable = false, unique = true, length = 50)
    var slug: String = "",
    
    @Column(name = "description", length = 200)
    var description: String? = null,
    
    @Column(name = "color", length = 20)
    var color: String? = null,
    
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
package com.blog.blogapi.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * Article entity - core blog content.
 */
@Entity
@Table(name = "articles")
data class Article(
    @field:NotBlank(message = "Article title is required")
    @field:Size(max = 200, message = "Title is too long")
    @Column(name = "title", nullable = false, length = 200)
    var title: String = "",
    
    @field:NotBlank(message = "Article slug is required")
    @field:Size(max = 200, message = "Slug is too long")
    @Column(name = "slug", nullable = false, length = 200)
    var slug: String = "",
    
    @Column(name = "summary", columnDefinition = "TEXT")
    var summary: String? = null,
    
    @Column(name = "content", columnDefinition = "TEXT")
    var content: String? = null,
    
    @Column(name = "content_html", columnDefinition = "TEXT")
    var contentHtml: String? = null,
    
    @Column(name = "cover_image_url", length = 500)
    var coverImageUrl: String? = null,
    
    @Column(name = "views", nullable = false)
    var views: Long = 0,
    
    @Column(name = "likes", nullable = false)
    var likes: Int = 0,
    
    @Column(name = "comments_count", nullable = false)
    var commentsCount: Int = 0,
    
    @Column(name = "reading_time_minutes")
    var readingTimeMinutes: Int? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: ArticleStatus = ArticleStatus.DRAFT,
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    var category: Category? = null,
    
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    var author: User,
    
    @ManyToMany
    @JoinTable(
        name = "article_tags",
        joinColumns = [JoinColumn(name = "article_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    var tags: MutableSet<Tag> = mutableSetOf(),
    
    @Column(name = "published_at")
    var publishedAt: java.time.LocalDateTime? = null,
    
    @Column(name = "allow_comments", nullable = false)
    var allowComments: Boolean = true,
    
    @Column(name = "is_featured", nullable = false)
    var isFeatured: Boolean = false,
    
    @Column(name = "is_recommended", nullable = false)
    var isRecommended: Boolean = false
) : BaseEntity() {
    
    fun incrementViews() {
        this.views++
    }
    
    fun incrementLikes() {
        this.likes++
    }
    
    fun decrementLikes() {
        if (this.likes > 0) {
            this.likes--
        }
    }
    
    fun incrementCommentsCount() {
        this.commentsCount++
    }
    
    fun decrementCommentsCount() {
        if (this.commentsCount > 0) {
            this.commentsCount--
        }
    }
}

/**
 * Article status enum
 */
enum class ArticleStatus {
    DRAFT,        // 草稿
    PENDING,      // 待审核
    PUBLISHED,    // 已发布
    ARCHIVED,     // 已归档
    PRIVATE,      // 私密
    DELETED       // 已删除
}
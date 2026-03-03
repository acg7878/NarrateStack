package com.blog.blogapi.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * Comment entity for article comments.
 */
@Entity
@Table(name = "comments")
data class Comment(
    @field:NotBlank(message = "Comment content is required")
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    var content: String = "",
    
    @Column(name = "content_html", columnDefinition = "TEXT")
    var contentHtml: String? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: CommentStatus = CommentStatus.PENDING,
    
    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    var article: Article,
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,
    
    @ManyToOne
    @JoinColumn(name = "parent_id")
    var parent: Comment? = null,
    
    @Column(name = "like_count", nullable = false)
    var likeCount: Int = 0,
    
    @Column(name = "is_author_reply", nullable = false)
    var isAuthorReply: Boolean = false,
    
    @Column(name = "ip_address", length = 45)
    var ipAddress: String? = null,
    
    @Column(name = "user_agent", length = 500)
    var userAgent: String? = null
) : BaseEntity() {
    
    fun incrementLikeCount() {
        this.likeCount++
    }
    
    fun decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--
        }
    }
}

/**
 * Comment status enum
 */
enum class CommentStatus {
    PENDING,    // 待审核
    APPROVED,   // 已通过
    REJECTED,   // 已拒绝
    DELETED     // 已删除
}
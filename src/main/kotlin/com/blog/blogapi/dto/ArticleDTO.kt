package com.blog.blogapi.dto

import com.blog.blogapi.entity.ArticleStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * DTO 对象用于文章相关接口的数据传输
 */
data class ArticleCreateRequest(
    @field:NotBlank(message = "文章标题不能为空")
    @field:Size(max = 200, message = "标题不能超过200个字符")
    val title: String = "",
    
    @field:NotBlank(message = "文章URL别名不能为空")
    @field:Size(max = 200, message = "URL别名不能超过200个字符")
    val slug: String = "",
    
    val summary: String? = null,              // 文章摘要，可选
    
    @field:NotBlank(message = "文章内容不能为空")
    val content: String = "",                 // Markdown格式的原始内容
    
    val contentHtml: String? = null,          // HTML格式的内容，后端生成
    
    val coverImageUrl: String? = null,        // 封面图片URL
    
    val categoryId: Long? = null,             // 分类ID，可选
    
    val tagIds: List<Long> = emptyList(),     // 标签ID列表
    
    val readingTimeMinutes: Int? = null,      // 预计阅读时间（分钟）
    
    val allowComments: Boolean = true,        // 是否允许评论
    
    val isFeatured: Boolean = false,          // 是否设置为推荐文章
    
    val isRecommended: Boolean = false,       // 是否设置为推荐阅读
    
    val status: ArticleStatus = ArticleStatus.DRAFT  // 文章状态，默认为草稿
)

data class ArticleUpdateRequest(
    @field:NotBlank(message = "文章标题不能为空")
    @field:Size(max = 200, message = "标题不能超过200个字符")
    val title: String = "",
    
    val summary: String? = null,
    
    @field:NotBlank(message = "文章内容不能为空")
    val content: String = "",
    
    val contentHtml: String? = null,
    
    val coverImageUrl: String? = null,
    
    val categoryId: Long? = null,
    
    val tagIds: List<Long> = emptyList(),
    
    val readingTimeMinutes: Int? = null,
    
    val allowComments: Boolean = true,
    
    val isFeatured: Boolean = false,
    
    val isRecommended: Boolean = false,
    
    val status: ArticleStatus = ArticleStatus.DRAFT
)

/**
 * 文章列表响应DTO，不包含完整内容
 */
data class ArticleListResponse(
    val id: Long,
    val title: String,
    val slug: String,
    val summary: String?,
    val coverImageUrl: String?,
    val views: Long,                          // 阅读量
    val likes: Int,                           // 点赞数
    val commentsCount: Int,                   // 评论数
    val readingTimeMinutes: Int?,
    val status: String,                       // 文章状态
    val category: CategorySimpleResponse?,    // 分类简单信息
    val author: UserSimpleResponse,           // 作者简单信息
    val tags: List<TagSimpleResponse>,        // 标签列表
    val publishedAt: String?,                 // 发布时间
    val allowComments: Boolean,
    val isFeatured: Boolean,
    val isRecommended: Boolean,
    val createdAt: String
)

/**
 * 文章详情响应DTO，包含完整内容
 */
data class ArticleDetailResponse(
    val id: Long,
    val title: String,
    val slug: String,
    val summary: String?,
    val content: String,                      // 完整的内容（Markdown格式）
    val contentHtml: String,                  // 完整的HTML内容
    val coverImageUrl: String?,
    val views: Long,
    val likes: Int,
    val commentsCount: Int,
    val readingTimeMinutes: Int?,
    val status: String,
    val category: CategorySimpleResponse?,
    val author: UserSimpleResponse,
    val tags: List<TagSimpleResponse>,
    val publishedAt: String?,
    val allowComments: Boolean,
    val isFeatured: Boolean,
    val isRecommended: Boolean,
    val createdAt: String,
    val updatedAt: String?
)

/**
 * 文章统计信息
 */
data class ArticleStatsResponse(
    val totalArticles: Long,                  // 文章总数
    val publishedArticles: Long,              // 已发布文章数
    val draftArticles: Long,                  // 草稿文章数
    val totalViews: Long,                     // 总阅读量
    val totalLikes: Long,                     // 总点赞数
    val totalComments: Long,                  // 总评论数
    val averageReadingTime: Double            // 平均阅读时长（分钟）
)
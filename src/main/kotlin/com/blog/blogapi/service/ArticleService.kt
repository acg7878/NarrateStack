package com.blog.blogapi.service

import com.blog.blogapi.dto.*
import com.blog.blogapi.entity.*
import com.blog.blogapi.repository.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository,
    private val tagRepository: TagRepository
) {
    
    /**
     * 获取文章列表（分页）
     */
    fun getArticles(pageable: Pageable): Page<ArticleListResponse> {
        return articleRepository.findByStatusAndIsDeletedFalseOrderByPublishedAtDesc(
            ArticleStatus.PUBLISHED, pageable
        ).map { article -> convertToListResponse(article) }
    }
    
    /**
     * 获取文章详情
     */
    fun getArticleById(id: Long): ArticleDetailResponse {
        val article = articleRepository.findById(id)
            .orElseThrow { IllegalArgumentException("文章不存在") }
        
        if (article.isDeleted) {
            throw IllegalArgumentException("文章已被删除")
        }
        
        article.incrementViews()
        articleRepository.save(article)
        
        return convertToDetailResponse(article)
    }
    
    /**
     * 创建文章
     */
    fun createArticle(request: ArticleCreateRequest, authorId: Long): ArticleDetailResponse {
        val author = userRepository.findById(authorId)
            .orElseThrow { IllegalArgumentException("作者不存在") }
        
        if (!author.isEnabled) {
            throw IllegalArgumentException("作者账号已禁用")
        }
        
        // 验证分类
        var category: Category? = null
        if (request.categoryId != null) {
            category = categoryRepository.findById(request.categoryId)
                .orElseThrow { IllegalArgumentException("分类不存在") }
        }
        
        // 验证标签
        val tags = mutableSetOf<Tag>()
        for (tagId in request.tagIds) {
            val tag = tagRepository.findById(tagId)
                .orElseThrow { IllegalArgumentException("标签不存在") }
            tags.add(tag)
        }
        
        val article = Article(
            title = request.title,
            slug = request.slug,
            summary = request.summary,
            content = request.content,
            contentHtml = request.contentHtml,
            coverImageUrl = request.coverImageUrl,
            readingTimeMinutes = request.readingTimeMinutes,
            status = request.status,
            category = category,
            author = author,
            tags = tags,
            allowComments = request.allowComments,
            isFeatured = request.isFeatured,
            isRecommended = request.isRecommended
        )
        
        if (request.status == ArticleStatus.PUBLISHED) {
            article.publishedAt = LocalDateTime.now()
        }
        
        val savedArticle = articleRepository.save(article)
        
        // 更新分类文章数量
        category?.incrementArticleCount()
        category?.let { categoryRepository.save(it) }
        
        // 更新标签文章数量
        tags.forEach { tag ->
            tag.incrementArticleCount()
            tagRepository.save(tag)
        }
        
        return convertToDetailResponse(savedArticle)
    }
    
    /**
     * 更新文章
     */
    fun updateArticle(id: Long, request: ArticleUpdateRequest, userId: Long): ArticleDetailResponse {
        val article = articleRepository.findById(id)
            .orElseThrow { IllegalArgumentException("文章不存在") }
        
        if (article.isDeleted) {
            throw IllegalArgumentException("文章已被删除")
        }
        
        // 检查权限
        if (article.author.id != userId) {
            throw IllegalArgumentException("无权修改此文章")
        }
        
        // 处理分类
        var newCategory: Category? = null
        if (request.categoryId != null && request.categoryId != (article.category?.id ?: 0)) {
            val oldCategory = article.category
            newCategory = categoryRepository.findById(request.categoryId)
                .orElseThrow { IllegalArgumentException("分类不存在") }
            
            // 更新分类计数
            oldCategory?.decrementArticleCount()
            oldCategory?.let { categoryRepository.save(it) }
            newCategory.incrementArticleCount()
            categoryRepository.save(newCategory)
        }
        
        // 处理标签
        val newTags = mutableSetOf<Tag>()
        for (tagId in request.tagIds) {
            val tag = tagRepository.findById(tagId)
                .orElseThrow { IllegalArgumentException("标签不存在") }
            newTags.add(tag)
        }
        
        // 更新标签计数
        val oldTags = article.tags.toSet()
        val addedTags = newTags.minus(oldTags)
        val removedTags = oldTags.minus(newTags)
        
        addedTags.forEach { tag ->
            tag.incrementArticleCount()
            tagRepository.save(tag)
        }
        
        removedTags.forEach { tag ->
            tag.decrementArticleCount()
            tagRepository.save(tag)
        }
        
        // 更新文章
        article.title = request.title
        article.summary = request.summary
        article.content = request.content
        article.contentHtml = request.contentHtml
        article.coverImageUrl = request.coverImageUrl
        article.readingTimeMinutes = request.readingTimeMinutes
        article.status = request.status
        article.category = newCategory ?: article.category
        article.tags = newTags
        article.allowComments = request.allowComments
        article.isFeatured = request.isFeatured
        article.isRecommended = request.isRecommended
        
        if (request.status == ArticleStatus.PUBLISHED && article.publishedAt == null) {
            article.publishedAt = LocalDateTime.now()
        }
        
        val updatedArticle = articleRepository.save(article)
        return convertToDetailResponse(updatedArticle)
    }
    
    // 转换方法（简化版，暂时实现基本逻辑）
    private fun convertToListResponse(article: Article): ArticleListResponse {
        return ArticleListResponse(
            id = article.id,
            title = article.title,
            slug = article.slug,
            summary = article.summary,
            coverImageUrl = article.coverImageUrl,
            views = article.views,
            likes = article.likes,
            commentsCount = article.commentsCount,
            readingTimeMinutes = article.readingTimeMinutes,
            status = article.status.name,
            category = null, // 待实现
            author = UserSimpleResponse(
                id = article.author.id,
                username = article.author.username,
                displayName = article.author.displayName,
                avatarUrl = article.author.avatarUrl
            ),
            tags = emptyList(), // 待实现
            publishedAt = article.publishedAt?.toString(),
            allowComments = article.allowComments,
            isFeatured = article.isFeatured,
            isRecommended = article.isRecommended,
            createdAt = article.createdAt?.toString() ?: LocalDateTime.now().toString()
        )
    }
    
    private fun convertToDetailResponse(article: Article): ArticleDetailResponse {
        return ArticleDetailResponse(
            id = article.id,
            title = article.title,
            slug = article.slug,
            summary = article.summary,
            content = article.content ?: "",
            contentHtml = article.contentHtml ?: "",
            coverImageUrl = article.coverImageUrl,
            views = article.views,
            likes = article.likes,
            commentsCount = article.commentsCount,
            readingTimeMinutes = article.readingTimeMinutes,
            status = article.status.name,
            category = null,
            author = UserSimpleResponse(
                id = article.author.id,
                username = article.author.username,
                displayName = article.author.displayName,
                avatarUrl = article.author.avatarUrl
            ),
            tags = emptyList(),
            publishedAt = article.publishedAt?.toString(),
            allowComments = article.allowComments,
            isFeatured = article.isFeatured,
            isRecommended = article.isRecommended,
            createdAt = article.createdAt?.toString() ?: LocalDateTime.now().toString(),
            updatedAt = article.updatedAt?.toString()
        )
    }
}
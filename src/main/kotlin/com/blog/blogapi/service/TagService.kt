package com.blog.blogapi.service

import com.blog.blogapi.dto.*
import com.blog.blogapi.entity.Tag
import com.blog.blogapi.repository.TagRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TagService(
    private val tagRepository: TagRepository
) {
    
    /**
     * 获取标签列表
     */
    fun getAllTags(): List<TagResponse> {
        return tagRepository.findAllByIsDeletedFalseOrderByArticleCountDesc().map { tag ->
            convertToTagResponse(tag)
        }
    }
    
    /**
     * 获取热门标签
     */
    fun getPopularTags(limit: Int = 10): List<TagSimpleResponse> {
        return tagRepository.findAllByIsDeletedFalseOrderByArticleCountDesc()
            .take(limit)
            .map { tag -> convertToTagSimpleResponse(tag) }
    }
    
    /**
     * 获取标签详情
     */
    fun getTagById(id: Long): TagResponse {
        val tag = tagRepository.findById(id)
            .orElseThrow { IllegalArgumentException("标签不存在") }
        
        if (tag.isDeleted) {
            throw IllegalArgumentException("标签已被删除")
        }
        
        return convertToTagResponse(tag)
    }
    
    /**
     * 通过别名获取标签
     */
    fun getTagBySlug(slug: String): TagResponse {
        val tag = tagRepository.findBySlugAndIsDeletedFalse(slug)
            ?: throw IllegalArgumentException("标签不存在")
        
        return convertToTagResponse(tag)
    }
    
    /**
     * 创建标签
     */
    fun createTag(request: TagCreateRequest): TagResponse {
        // 验证唯一性
        if (tagRepository.existsBySlugAndIsDeletedFalse(request.slug)) {
            throw IllegalArgumentException("标签别名已存在")
        }
        
        val tag = Tag(
            name = request.name,
            slug = request.slug,
            description = request.description,
            color = request.color,
            articleCount = 0
        )
        
        val savedTag = tagRepository.save(tag)
        return convertToTagResponse(savedTag)
    }
    
    /**
     * 更新标签
     */
    fun updateTag(id: Long, request: TagUpdateRequest): TagResponse {
        val tag = tagRepository.findById(id)
            .orElseThrow { IllegalArgumentException("标签不存在") }
        
        if (tag.isDeleted) {
            throw IllegalArgumentException("标签已被删除")
        }
        
        tag.name = request.name
        tag.description = request.description
        tag.color = request.color
        
        val updatedTag = tagRepository.save(tag)
        return convertToTagResponse(updatedTag)
    }
    
    /**
     * 删除标签
     */
    fun deleteTag(id: Long): Boolean {
        val tag = tagRepository.findById(id)
            .orElseThrow { IllegalArgumentException("标签不存在") }
        
        if (tag.articleCount > 0) {
            throw IllegalArgumentException("标签有文章关联无法删除")
        }
        
        tag.isDeleted = true
        tagRepository.save(tag)
        return true
    }
    
    /**
     * 搜索标签
     */
    fun searchTags(query: String, pageable: Pageable): Page<TagResponse> {
        return tagRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(query, pageable)
            .map { tag -> convertToTagResponse(tag) }
    }
    
    /**
     * 获取标签统计
     */
    fun getTagStats(): TagStatsResponse {
        val tags = tagRepository.findAllByIsDeletedFalse()
        if (tags.isEmpty()) {
            return TagStatsResponse(
                totalTags = 0,
                mostUsedTag = "",
                mostUsedTagCount = 0,
                averageUsage = 0.0
            )
        }
        
        val mostUsedTag = tags.maxByOrNull { it.articleCount }
        val totalTags = tags.size.toLong()
        val averageUsage = tags.sumOf { it.articleCount }.toDouble() / totalTags
        
        return TagStatsResponse(
            totalTags = totalTags,
            mostUsedTag = mostUsedTag?.name ?: "",
            mostUsedTagCount = mostUsedTag?.articleCount?.toLong() ?: 0,
            averageUsage = averageUsage
        )
    }
    
    /**
     * 批量创建标签
     */
    fun batchCreateTags(tagNames: List<String>): List<TagResponse> {
        val results = mutableListOf<TagResponse>()
        
        for (tagName in tagNames.distinct()) {
            if (tagName.isBlank()) continue
            
            val slug = tagName.lowercase().replace("\\s+".toRegex(), "-")
            val existingTag = tagRepository.findBySlugAndIsDeletedFalse(slug)
            
            if (existingTag != null) {
                results.add(convertToTagResponse(existingTag))
                continue
            }
            
            val tag = Tag(
                name = tagName.trim(),
                slug = slug,
                description = null,
                color = null,
                articleCount = 0
            )
            
            val savedTag = tagRepository.save(tag)
            results.add(convertToTagResponse(savedTag))
        }
        
        return results
    }
    
    /**
     * 清理未使用的标签
     */
    fun cleanupUnusedTags(): Int {
        val unusedTags = tagRepository.findAllByIsDeletedFalse()
            .filter { it.articleCount == 0 }
        
        unusedTags.forEach { tag ->
            tag.isDeleted = true
            tagRepository.save(tag)
        }
        
        return unusedTags.size
    }
    
    // 私有辅助方法
    private fun convertToTagResponse(tag: Tag): TagResponse {
        return TagResponse(
            id = tag.id,
            name = tag.name,
            slug = tag.slug,
            description = tag.description,
            color = tag.color,
            articleCount = tag.articleCount,
            createdAt = tag.createdAt?.toString() ?: LocalDateTime.now().toString(),
            updatedAt = tag.updatedAt?.toString()
        )
    }
    
    private fun convertToTagSimpleResponse(tag: Tag): TagSimpleResponse {
        return TagSimpleResponse(
            id = tag.id,
            name = tag.name,
            slug = tag.slug,
            color = tag.color,
            articleCount = tag.articleCount
        )
    }
}
package com.blog.blogapi.service

import com.blog.blogapi.dto.*
import com.blog.blogapi.entity.Category
import com.blog.blogapi.repository.CategoryRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository
) {
    
    /**
     * 获取分类列表
     */
    fun getAllCategories(): List<CategoryResponse> {
        return categoryRepository.findAllByIsDeletedFalseOrderBySortOrderAsc().map { category ->
            convertToCategoryResponse(category)
        }
    }
    
    /**
     * 获取分类树
     */
    fun getCategoryTree(): List<CategoryTreeResponse> {
        val allCategories = categoryRepository.findAllByIsDeletedFalse()
        val topLevelCategories = allCategories.filter { it.parentId == null }
        
        return topLevelCategories.map { category ->
            buildCategoryTree(category, allCategories)
        }
    }
    
    /**
     * 获取分类详情
     */
    fun getCategoryById(id: Long): CategoryResponse {
        val category = categoryRepository.findById(id)
            .orElseThrow { IllegalArgumentException("分类不存在") }
        
        if (category.isDeleted) {
            throw IllegalArgumentException("分类已被删除")
        }
        
        val children = categoryRepository.findAllByParentIdAndIsDeletedFalse(category.id)
        return convertToCategoryResponse(category).copy(
            children = children.map { convertToCategorySimpleResponse(it) }
        )
    }
    
    /**
     * 创建分类
     */
    fun createCategory(request: CategoryCreateRequest): CategoryResponse {
        // 验证唯一性
        if (categoryRepository.existsBySlugAndIsDeletedFalse(request.slug)) {
            throw IllegalArgumentException("分类别名已存在")
        }
        
        // 验证父分类
        var parentCategory: Category? = null
        if (request.parentId != null) {
            parentCategory = categoryRepository.findById(request.parentId)
                .orElseThrow { IllegalArgumentException("父分类不存在") }
        }
        
        val category = Category(
            name = request.name,
            slug = request.slug,
            description = request.description,
            icon = request.icon,
            color = request.color,
            sortOrder = request.sortOrder,
            parentId = request.parentId,
            articleCount = 0
        )
        
        val savedCategory = categoryRepository.save(category)
        return convertToCategoryResponse(savedCategory)
    }
    
    /**
     * 更新分类
     */
    fun updateCategory(id: Long, request: CategoryUpdateRequest): CategoryResponse {
        val category = categoryRepository.findById(id)
            .orElseThrow { IllegalArgumentException("分类不存在") }
        
        if (category.isDeleted) {
            throw IllegalArgumentException("分类已被删除")
        }
        
        // 验证父分类不能是自己
        if (request.parentId == id) {
            throw IllegalArgumentException("分类不能是自己的父分类")
        }
        
        // 验证父分类存在
        if (request.parentId != null) {
            val parentCategory = categoryRepository.findById(request.parentId)
                .orElseThrow { IllegalArgumentException("父分类不存在") }
            if (parentCategory.isDeleted) {
                throw IllegalArgumentException("父分类已被删除")
            }
        }
        
        // 检查循环引用
        if (request.parentId != null) {
            val visited = mutableSetOf<Long>(id)
            var currentId: Long? = request.parentId
            while (currentId != null) {
                if (visited.contains(currentId)) {
                    throw IllegalArgumentException("检测到循环引用")
                }
                visited.add(currentId)
                val currentCategory = categoryRepository.findById(currentId)
                    .orElseThrow { IllegalArgumentException("分类不存在") }
                currentId = currentCategory.parentId
            }
        }
        
        category.name = request.name
        category.description = request.description
        category.icon = request.icon
        category.color = request.color
        category.sortOrder = request.sortOrder
        category.parentId = request.parentId
        
        val updatedCategory = categoryRepository.save(category)
        return convertToCategoryResponse(updatedCategory)
    }
    
    /**
     * 删除分类
     */
    fun deleteCategory(id: Long): Boolean {
        val category = categoryRepository.findById(id)
            .orElseThrow { IllegalArgumentException("分类不存在") }
        
        if (category.articleCount > 0) {
            throw IllegalArgumentException("分类下有文章无法删除")
        }
        
        // 检查子分类
        val children = categoryRepository.findAllByParentIdAndIsDeletedFalse(id)
        if (children.isNotEmpty()) {
            throw IllegalArgumentException("分类有子分类无法删除")
        }
        
        category.isDeleted = true
        categoryRepository.save(category)
        return true
    }
    
    /**
     * 搜索分类
     */
    fun searchCategories(query: String, pageable: Pageable): Page<CategoryResponse> {
        return categoryRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(query, pageable)
            .map { category -> convertToCategoryResponse(category) }
    }
    
    /**
     * 获取分类统计
     */
    fun getCategoryStats(): CategoryStatsResponse {
        val categories = categoryRepository.findAllByIsDeletedFalse()
        val totalCategories = categories.size.toLong()
        val categoriesWithArticles = categories.count { it.articleCount > 0 }.toLong()
        val averageArticles = if (totalCategories > 0) {
            categories.sumOf { it.articleCount }.toDouble() / totalCategories
        } else 0.0
        
        return CategoryStatsResponse(
            totalCategories = totalCategories,
            categoriesWithArticles = categoriesWithArticles,
            averageArticlesPerCategory = averageArticles
        )
    }
    
    // 私有辅助方法
    private fun buildCategoryTree(category: Category, allCategories: List<Category>): CategoryTreeResponse {
        val children = allCategories.filter { it.parentId == category.id }
            .map { child -> buildCategoryTree(child, allCategories) }
            .sortedBy { it.sortOrder }
        
        return CategoryTreeResponse(
            id = category.id,
            name = category.name,
            slug = category.slug,
            icon = category.icon,
            color = category.color,
            articleCount = category.articleCount,
            children = if (children.isEmpty()) null else children
        )
    }
    
    private fun convertToCategoryResponse(category: Category): CategoryResponse {
        return CategoryResponse(
            id = category.id,
            name = category.name,
            slug = category.slug,
            description = category.description,
            icon = category.icon,
            color = category.color,
            sortOrder = category.sortOrder,
            parentId = category.parentId,
            articleCount = category.articleCount,
            children = null,
            createdAt = category.createdAt?.toString() ?: LocalDateTime.now().toString(),
            updatedAt = category.updatedAt?.toString()
        )
    }
    
    private fun convertToCategorySimpleResponse(category: Category): CategorySimpleResponse {
        return CategorySimpleResponse(
            id = category.id,
            name = category.name,
            slug = category.slug,
            icon = category.icon,
            color = category.color,
            articleCount = category.articleCount
        )
    }
}
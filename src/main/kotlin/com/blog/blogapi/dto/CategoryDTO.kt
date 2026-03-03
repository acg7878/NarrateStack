package com.blog.blogapi.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * DTO 对象用于分类相关的数据传输
 */
data class CategoryCreateRequest(
    @field:NotBlank(message = "分类名称不能为空")
    @field:Size(max = 100, message = "分类名称不能超过100个字符")
    val name: String = "",          // 分类名称
    
    @field:NotBlank(message = "分类别名不能为空")
    @field:Size(max = 100, message = "分类别名不能超过100个字符")
    val slug: String = "",          // 分类URL别名
    
    val description: String? = null,    // 分类描述
    val icon: String? = null,           // 图标名称或URL
    val color: String? = null,          // 分类颜色
    val sortOrder: Int = 0,             // 排序顺序
    val parentId: Long? = null          // 父分类ID，可为空
)

data class CategoryUpdateRequest(
    @field:NotBlank(message = "分类名称不能为空")
    @field:Size(max = 100, message = "分类名称不能超过100个字符")
    val name: String = "",
    
    val description: String? = null,
    val icon: String? = null,
    val color: String? = null,
    val sortOrder: Int = 0,
    val parentId: Long? = null
)

data class CategoryResponse(
    val id: Long,                   // 分类ID
    val name: String,               // 分类名称
    val slug: String,               // 分类URL别名
    val description: String?,       // 分类描述
    val icon: String?,              // 图标
    val color: String?,             // 颜色
    val sortOrder: Int,             // 排序顺序
    val parentId: Long?,            // 父分类ID
    val articleCount: Int,          // 文章数量
    val children: List<CategorySimpleResponse>?,  // 子分类列表
    val createdAt: String,          // 创建时间
    val updatedAt: String?          // 更新时间
)

data class CategorySimpleResponse(
    val id: Long,                   // 分类ID
    val name: String,               // 分类名称
    val slug: String,               // 分类URL别名
    val icon: String?,              // 图标
    val color: String?,             // 颜色
    val articleCount: Int           // 文章数量
)

data class CategoryTreeResponse(
    val id: Long,                   // 分类ID
    val name: String,               // 分类名称
    val slug: String,               // 分类URL别名
    val icon: String?,              // 图标
    val color: String?,             // 颜色
    val articleCount: Int,          // 文章数量
    val children: List<CategoryTreeResponse>?  // 子分类（递归结构）
)

data class CategoryStatsResponse(
    val totalCategories: Long,             // 总分类数
    val categoriesWithArticles: Long,      // 有文章的分类数
    val averageArticlesPerCategory: Double // 每个分类的平均文章数
)
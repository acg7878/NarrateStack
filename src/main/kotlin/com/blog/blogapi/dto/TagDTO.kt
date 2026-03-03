package com.blog.blogapi.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * DTO 对象用于标签相关的数据传输
 */
data class TagCreateRequest(
    @field:NotBlank(message = "标签名称不能为空")
    @field:Size(max = 50, message = "标签名称不能超过50个字符")
    val name: String = "",           // 标签名称
    
    @field:NotBlank(message = "标签别名不能为空")
    @field:Size(max = 50, message = "标签别名不能超过50个字符")
    val slug: String = "",           // 标签URL别名
    
    val description: String? = null,    // 标签描述
    val color: String? = null           // 标签颜色
)

data class TagUpdateRequest(
    @field:NotBlank(message = "标签名称不能为空")
    @field:Size(max = 50, message = "标签名称不能超过50个字符")
    val name: String = "",
    
    val description: String? = null,
    val color: String? = null
)

data class TagResponse(
    val id: Long,                   // 标签ID
    val name: String,               // 标签名称
    val slug: String,               // 标签URL别名
    val description: String?,       // 标签描述
    val color: String?,             // 标签颜色
    val articleCount: Int,          // 文章数量
    val createdAt: String,          // 创建时间
    val updatedAt: String?          // 更新时间
)

data class TagSimpleResponse(
    val id: Long,                   // 标签ID
    val name: String,               // 标签名称
    val slug: String,               // 标签URL别名
    val color: String?,             // 标签颜色
    val articleCount: Int           // 文章数量
)

data class TagStatsResponse(
    val totalTags: Long,            // 总标签数
    val mostUsedTag: String,        // 使用最多的标签
    val mostUsedTagCount: Long,     // 最多标签使用次数
    val averageUsage: Double        // 平均每个标签使用次数
)
package com.blog.blogapi.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import java.time.LocalDateTime
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

/**
 * Base entity containing common audit fields:
 * ID, createdAt, updatedAt.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
    
    @Column(name = "created_at")
    @CreatedDate
    @get:Temporal(TemporalType.TIMESTAMP)
    var createdAt: LocalDateTime? = null
    
    @Column(name = "updated_at")
    @LastModifiedDate
    @get:Temporal(TemporalType.TIMESTAMP)
    var updatedAt: LocalDateTime? = null
    
    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean = false
}
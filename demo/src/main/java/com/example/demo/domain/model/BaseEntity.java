package com.example.demo.domain.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseEntity {
    
    @Column(
        name = "uuid",
        nullable = false,
        unique = true,
        insertable = false,
        updatable = false,
        columnDefinition = "UUID DEFAULT uuid_generate_v4()"
    )
    protected UUID uuid;

    protected abstract UUID getUuid();

    protected abstract void setUuid(UUID uuid);

}

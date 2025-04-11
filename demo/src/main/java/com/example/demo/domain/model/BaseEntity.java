package com.example.demo.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.UpdateTimestamp;

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
    @Generated(GenerationTime.INSERT)
    protected UUID uuid;

    @Column(name = "criado_em", nullable = false, updatable = false)
    @CreationTimestamp
    protected LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    @UpdateTimestamp
    protected LocalDateTime atualizadoEm;

    protected abstract UUID getUuid();

    protected abstract void setUuid(UUID uuid);

    protected abstract LocalDateTime getCriadoEm();

    protected abstract void setCriadoEm(LocalDateTime criadoEm);

    protected abstract LocalDateTime getAtualizadoEm();

    protected abstract void setAtualizadoEm(LocalDateTime atualizadoEm);
}

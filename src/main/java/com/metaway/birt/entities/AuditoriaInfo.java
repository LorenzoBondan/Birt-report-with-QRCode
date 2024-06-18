package com.metaway.birt.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
@ToString
public class AuditoriaInfo {

    @CreatedBy
    @Column(name = "criado_por")
    private String criadoPor;

    @CreationTimestamp
    @Column(name = "criado_em")
    private LocalDateTime criadoEm;
}

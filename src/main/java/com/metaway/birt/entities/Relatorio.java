package com.metaway.birt.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "cdrel", callSuper = false)
@Entity
@Table(name = "relatorio", schema = "public")
public class Relatorio extends AuditoriaInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cdrel;
    @Column(unique = true, nullable = false, updatable = false, columnDefinition = "TEXT")
    private String hash;
    @Column(name = "content_type")
    private String contentType;
    private String checksum;
    private byte[] xml;
    private byte[] pdf;

    @PrePersist
    protected void onCreate() {
        this.hash = UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return "Relatorio{" +
                "cdrel=" + cdrel +
                ", hash='" + hash + '\'' +
                ", " + super.toString() +
                '}';
    }
}

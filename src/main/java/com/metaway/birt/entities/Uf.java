package com.metaway.birt.entities;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "cduf")
@Entity
@Table(name = "uf", schema = "public")
public class Uf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cduf;
    private String dsuf;
    private String sguf;

    @ManyToOne
    @JoinColumn(name = "cdpais", referencedColumnName = "cdpai")
    private Pais pais;

    private Integer codext;
}

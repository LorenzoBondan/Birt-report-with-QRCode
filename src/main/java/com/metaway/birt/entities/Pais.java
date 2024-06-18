package com.metaway.birt.entities;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "cdpai")
@Entity
@Table(name = "pais", schema = "public")
public class Pais {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cdpai;
    private String dspai;
    @Column(name = "nacionalidadem")
    private String nacionalidade_m;
    @Column(name = "nacionalidadef")
    private String nacionalidade_f;
    private Integer codext;
}

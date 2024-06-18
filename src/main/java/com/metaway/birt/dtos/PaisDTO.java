package com.metaway.birt.dtos;

import com.metaway.birt.entities.Pais;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaisDTO {

    private Long cdpai;
    private String dspai;
    private String nacionalidade_m;
    private String nacionalidade_f;
    private Integer codext;

    public PaisDTO(Pais pais) {
        this.cdpai = pais.getCdpai();
        this.dspai = pais.getDspai();
        this.nacionalidade_m = pais.getNacionalidade_m();
        this.nacionalidade_f = pais.getNacionalidade_f();
        this.codext = pais.getCodext();
    }
}

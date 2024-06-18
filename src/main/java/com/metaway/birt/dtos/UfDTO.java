package com.metaway.birt.dtos;

import com.metaway.birt.entities.Uf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UfDTO {

    private Long cduf;
    private String dsuf;
    private String sguf;
    private Integer codext;
    private PaisDTO pais;

    public UfDTO(Uf uf) {
        this.cduf = uf.getCduf();
        this.dsuf = uf.getDsuf();
        this.sguf = uf.getSguf();
        this.codext = uf.getCodext();
        this.pais = new PaisDTO(uf.getPais());
    }
}

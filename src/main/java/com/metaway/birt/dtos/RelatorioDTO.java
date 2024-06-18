package com.metaway.birt.dtos;

import com.metaway.birt.entities.Relatorio;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RelatorioDTO {

    private Long cdrel;
    private String criadoPor;
    private LocalDateTime criadoEm;
    private String hash;
    private String contentType;
    private String checksum;
    private byte[] xml;
    private byte[] pdf;

    public RelatorioDTO(Relatorio relatorio) {
        this.cdrel = relatorio.getCdrel();
        this.criadoPor = relatorio.getCriadoPor();
        this.criadoEm = relatorio.getCriadoEm();
        this.hash = relatorio.getHash();
        this.contentType = relatorio.getContentType();
        this.checksum = relatorio.getChecksum();
        this.xml = relatorio.getXml();
        this.pdf = relatorio.getPdf();
    }
}

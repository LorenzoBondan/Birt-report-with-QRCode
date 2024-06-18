package com.metaway.birt.designhandler;

import com.metaway.birt.entities.Relatorio;
import com.metaway.birt.repositories.RelatorioRepository;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.model.api.LabelHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.SimpleMasterPageHandle;
import org.eclipse.birt.report.model.api.SlotHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.command.ContentException;
import org.eclipse.birt.report.model.api.command.NameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Métodos auxiliares para manipulação do relatório
 */
@Component
public class ReportUtilsHandler {

    @Autowired
    private RelatorioRepository relatorioRepository;

    /**
     * Limpar o corpo do relatório
     */
    public void clearBody(ReportDesignHandle designHandle) throws SemanticException {
        SlotHandle bodySlot = designHandle.getBody();
        while (bodySlot.getCount() > 0) {
            bodySlot.drop(bodySlot.get(0));
        }
    }

    /**
     * Limpar header/footer
     */
    public void clearSlot(SlotHandle slot) throws SemanticException {
        while (slot.getCount() > 0) {
            slot.drop(slot.get(0));
        }
    }

    /**
     * Buscar ou criar a página principal do relatório
     */
    public SimpleMasterPageHandle getOrCreateMasterPage(ReportDesignHandle designHandle) throws NameException, ContentException {
        if (designHandle.getMasterPages().getCount() > 0) {
            return (SimpleMasterPageHandle) designHandle.getMasterPages().get(0);
        } else {
            SimpleMasterPageHandle masterPage = designHandle.getElementFactory().newSimpleMasterPage("Master Page");
            designHandle.getMasterPages().add(masterPage);
            return masterPage;
        }
    }

    /**
     * Alterar o texto de uma Label já existente no design do relatório
     */
    public void changeLabelText(IReportRunnable design, String labelName, String labelText) throws SemanticException {
        ReportDesignHandle report = (ReportDesignHandle) design.getDesignHandle();

        // Encontra o elemento de texto (label)
        LabelHandle label = (LabelHandle) report.findElement(labelName);

        // Altera o conteúdo da label
        label.setText(labelText);
    }

    /**
     * Armazena o XML gerado como um array de bytes no objeto Relatorio
     */
    public void registerXmlAttributeAsByteArray(IReportRunnable design, Relatorio relatorio) throws IOException {
        // Gerar e ler o arquivo temporário
        File tempFile = File.createTempFile("temp_report_", ".rptdesign");

        // Salvar o design modificado no arquivo temporário
        ((ReportDesignHandle) design.getDesignHandle()).saveAs(tempFile.getAbsolutePath());

        try {
            // Ler o conteúdo do arquivo temporário como um array de bytes
            byte[] content = Files.readAllBytes(tempFile.toPath());
            relatorio.setXml(content);
            relatorioRepository.save(relatorio);
        } finally {
            // Deletar o arquivo temporário
            tempFile.delete();
        }
    }
}

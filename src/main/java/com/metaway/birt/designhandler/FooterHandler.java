package com.metaway.birt.designhandler;

import com.metaway.birt.entities.Relatorio;
import org.eclipse.birt.report.model.api.*;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FooterHandler {

    @Autowired
    private ReportUtilsHandler reportUtilsHandler;
    @Autowired
    private ImageHandler imageHandler;
    @Autowired
    private StylesHandler stylesHandler;

    /**
     * Adicionar footer
     */
    public void addFooter(ReportDesignHandle designHandle, Relatorio relatorio) throws SemanticException, IOException {
        // Obter ou criar a página mestre
        SimpleMasterPageHandle masterPage = reportUtilsHandler.getOrCreateMasterPage(designHandle);

        // Limpar o slot do rodapé se necessário
        reportUtilsHandler.clearSlot(masterPage.getPageFooter());

        // Definir a altura do rodapé
        masterPage.setProperty("footerHeight", "1in"); // Definindo a altura do rodapé

        // Criar um Grid para o rodapé
        GridHandle footerGrid = designHandle.getElementFactory().newGridItem(null, 2, 1);
        footerGrid.setWidth("100%");

        // Definir a altura da linha do rodapé
        RowHandle footerRow = (RowHandle) footerGrid.getRows().get(0);
        footerRow.setProperty("height", "1in"); // Aumentar a altura do rodapé para acomodar o QR code

        CellHandle footerCell = (CellHandle) footerRow.getCells().get(0);

        // Criar um novo estilo para ser aplicado ao rodapé
        SharedStyleHandle newStyle = stylesHandler.getStandardStyle(designHandle);

        // Criar um TextItem para o rodapé
        TextItemHandle footerText = designHandle.getElementFactory().newTextItem(null);
        footerText.setContent("This is the Footer");
        footerText.setStyle(newStyle);
        footerCell.getContent().add(footerText);

        // Adicionar QR Code ao rodapé (na segunda coluna) '
        CellHandle qrCodeCell = (CellHandle) footerRow.getCells().get(1);
        qrCodeCell.getContent().add(imageHandler.addQrCode(designHandle, relatorio));

        qrCodeCell.setProperty(StyleHandle.TEXT_ALIGN_PROP, DesignChoiceConstants.TEXT_ALIGN_RIGHT);

        // Adicionar o Grid ao rodapé da página mestre
        masterPage.getPageFooter().add(footerGrid);
    }
}
package com.metaway.birt.designhandler;

import org.eclipse.birt.report.model.api.*;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HeaderHandler {

    @Autowired
    private ReportUtilsHandler reportUtilsHandler;
    @Autowired
    private ImageHandler imageHandler;
    @Autowired
    private StylesHandler stylesHandler;

    /**
     * Adicionar header
     */
    public void addHeader(ReportDesignHandle designHandle) throws SemanticException, IOException {
        // Obter ou criar a página mestre
        SimpleMasterPageHandle masterPage = reportUtilsHandler.getOrCreateMasterPage(designHandle);

        // Limpar o slot do cabeçalho se necessário
        reportUtilsHandler.clearSlot(masterPage.getPageHeader());

        // Criar um Grid (div) para o cabeçalho com 2 colunas e 1 linha
        GridHandle headerGrid = designHandle.getElementFactory().newGridItem(null, 2, 1);
        headerGrid.setWidth("100%");
        RowHandle headerRow = (RowHandle) headerGrid.getRows().get(0);

        // Definir a altura da linha do cabeçalho
        headerRow.setProperty("height", "400px");

        // Definir a largura das colunas (para uma largura igual, esse passo pode ser apagado)
        ColumnHandle column1 = (ColumnHandle) headerGrid.getColumns().get(0);
        column1.setProperty("width", "20%");
        ColumnHandle column2 = (ColumnHandle) headerGrid.getColumns().get(1);
        column2.setProperty("width", "80%");

        // Criar um novo estilo para ser aplicado ao cabeçalho
        SharedStyleHandle newStyle = stylesHandler.getStandardStyle(designHandle);

        // Adicionar a imagem embutida
        ImageHandle headerImage = imageHandler.addEmbeddedImage(designHandle);

        // Adicionar a imagem ao cabeçalho (na primeira coluna)
        CellHandle imageCell = (CellHandle) headerRow.getCells().get(0);
        imageCell.setProperty(StyleHandle.TEXT_ALIGN_PROP, DesignChoiceConstants.TEXT_ALIGN_LEFT);
        imageCell.getContent().add(headerImage);

        // Criar um TextItem para o cabeçalho
        TextItemHandle headerText = designHandle.getElementFactory().newTextItem(null);
        headerText.setContentType(DesignChoiceConstants.TEXT_CONTENT_TYPE_HTML);
        headerText.setContent("Relatório de análises");
        headerText.setProperty(StyleHandle.FONT_SIZE_PROP, "16pt");
        headerText.setProperty(StyleHandle.COLOR_PROP, "#333333");
        headerText.setProperty(StyleHandle.TEXT_ALIGN_PROP, DesignChoiceConstants.TEXT_ALIGN_LEFT);

        // Adicionar o TextItem ao cabeçalho (na segunda coluna)
        CellHandle textCell = (CellHandle) headerRow.getCells().get(1);
        textCell.getContent().add(headerText);

        // Adicionar o Grid ao cabeçalho da página mestre
        headerGrid.setStyle(newStyle);
        masterPage.getPageHeader().add(headerGrid);
    }
}

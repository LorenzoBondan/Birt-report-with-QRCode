package com.metaway.birt.designhandler;

import com.metaway.birt.dtos.PaisDTO;
import com.metaway.birt.dtos.UfDTO;
import com.metaway.birt.services.PaisService;
import com.metaway.birt.services.UfService;
import org.eclipse.birt.report.model.api.*;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;

@Component
public class TableHandler {

    @Autowired
    private PaisService paisService;
    @Autowired
    private UfService ufService;

    /**
     * Método genérico para adicionar tabelas. Adiciona todos os campos encontrados no objeto
     */
    public void addTable(ReportDesignHandle designHandle, List<?> data, String tableName) throws SemanticException, IllegalAccessException {
        if (data == null || data.isEmpty()) {
            System.err.println("Nenhum dado encontrado.");
            return;
        }

        // Definir o número de colunas baseado no primeiro objeto da lista
        Object firstObject = data.getFirst();
        Field[] fields = firstObject.getClass().getDeclaredFields();
        int columnNumber = fields.length;

        // Definir cores alternadas para as linhas
        String corBranca = "#FFFFFF"; // Branco
        String corCinza = "#F0F0F0"; // Cinza claro

        // Criar uma tabela para exibir os dados
        TableHandle table = designHandle.getElementFactory().newTableItem(tableName, columnNumber);
        table.setWidth("100%");

        // Configurar as colunas da tabela
        for (int i = 0; i < columnNumber; i++) {
            ColumnHandle column = (ColumnHandle) table.getColumns().get(i);
            int columnWidth = 100 / columnNumber;
            column.setProperty("width", columnWidth + "%");
        }

        // Configurar o cabeçalho da tabela
        RowHandle header = (RowHandle) table.getHeader().get(0);

        // Adicionar células se não existirem
        while (header.getCells().getCount() < columnNumber) {
            header.getCells().add(designHandle.getElementFactory().newCell());
        }

        // Adicionar cada célula do cabeçalho
        for (int i = 0; i < fields.length; i++) {
            addHeaderCell(designHandle, header, fields[i].getName(), i);
        }

        // Adicionar linhas de detalhes com dados
        String currentRowColor = corBranca; // Começa com a cor branca
        for (Object obj : data) {
            RowHandle detailRow = designHandle.getElementFactory().newTableRow();
            table.getDetail().add(detailRow);

            for (Field field : fields) {
                field.setAccessible(true); // Permitir acesso a campos privados
                Object value = field.get(obj);

                if (value != null) {
                    Class<?> fieldType = field.getType();
                    if (isPrimitiveOrWrapper(fieldType) || fieldType == String.class) {
                        addDetailCell(designHandle, detailRow, value.toString(), currentRowColor);
                    } else {
                        // Trata como objeto complexo
                        Field[] subFields = fieldType.getDeclaredFields();
                        String displayValue = getFirstStringFieldValue(value, subFields);
                        addDetailCell(designHandle, detailRow, displayValue, currentRowColor);
                    }
                } else {
                    addDetailCell(designHandle, detailRow, "", currentRowColor);
                }
            }

            // Alternar a cor para a próxima linha
            currentRowColor = currentRowColor.equals(corBranca) ? corCinza : corBranca;
        }

        table.setProperty(StyleHandle.MARGIN_BOTTOM_PROP, "10px");

        // Adicionar a tabela ao corpo do relatório
        designHandle.getBody().add(table);
    }

    /**
     * Busca o valor do primeiro campo de String de um objeto
     * Em um relacionamento, traz a 'descrição' do objeto para mostrar na tabela
     */
    private String getFirstStringFieldValue(Object obj, Field[] fields) throws IllegalAccessException {
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getType() == String.class) {
                return (String) field.get(obj);
            }
        }
        return obj.getClass().getName(); // Retorna o nome da classe se nenhum campo String for encontrado
    }

    /**
     * Define se um atributo é um valor primitivo, como String ou Integer, ou é um objeto (relacionamento)
     */
    private boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive() ||
                type == Boolean.class || type == Integer.class ||
                type == Character.class || type == Byte.class ||
                type == Short.class || type == Double.class ||
                type == Long.class || type == Float.class;
    }

    /**
     * Método específico para adicionar a tabela de países (alterando o nome das colunas)
     */
    public void addPaisesTable(ReportDesignHandle designHandle) throws SemanticException {
        List<PaisDTO> paises = paisService.findAll();

        if (paises == null || paises.isEmpty()) {
            System.err.println("Nenhum país encontrado.");
            return;
        }

        // Definir o número de colunas
        int columnNumber = 4;

        // Definir cores alternadas para as linhas
        String corBranca = "#FFFFFF"; // Branco
        String corCinza = "#F0F0F0"; // Cinza claro

        // Criar uma tabela para exibir os dados
        TableHandle table = designHandle.getElementFactory().newTableItem("PaisTable", columnNumber);
        table.setWidth("100%");

        // Configurar as colunas da tabela
        for (int i = 0; i < columnNumber; i++) {
            ColumnHandle column = (ColumnHandle) table.getColumns().get(i);
            int columnWidth = 100 / columnNumber;
            column.setProperty("width", columnWidth + "%");
        }

        // Configurar o cabeçalho da tabela
        RowHandle header = (RowHandle) table.getHeader().get(0);

        // Adicionar células se não existirem
        while (header.getCells().getCount() < columnNumber) {
            header.getCells().add(designHandle.getElementFactory().newCell());
        }

        // Adicionar cada célula do cabeçalho
        addHeaderCell(designHandle, header, "ID", 0);
        addHeaderCell(designHandle, header, "Nome", 1);
        addHeaderCell(designHandle, header, "Nacionalidade M", 2);
        addHeaderCell(designHandle, header, "Nacionalidade F", 3);

        // Adicionar linhas de detalhes com dados
        String currentRowColor = corBranca; // Começa com a cor branca
        for (PaisDTO pais : paises) {
            RowHandle detailRow = designHandle.getElementFactory().newTableRow();
            table.getDetail().add(detailRow);

            addDetailCell(designHandle, detailRow, pais.getCdpai().toString(), currentRowColor);
            addDetailCell(designHandle, detailRow, pais.getDspai(), currentRowColor);
            addDetailCell(designHandle, detailRow, pais.getNacionalidade_m(), currentRowColor);
            addDetailCell(designHandle, detailRow, pais.getNacionalidade_f(), currentRowColor);

            // Alternar a cor para a próxima linha
            currentRowColor = currentRowColor.equals(corBranca) ? corCinza : corBranca;
        }

        table.setProperty(StyleHandle.MARGIN_BOTTOM_PROP, "10px");

        // Adicionar a tabela ao corpo do relatório
        designHandle.getBody().add(table);
    }

    /**
     * Método específico para adicionar a tabela de UFs (alterando o nome das colunas)
     */
    public void addUfTable(ReportDesignHandle designHandle) throws SemanticException {
        List<UfDTO> ufs = ufService.findAll();

        if (ufs == null || ufs.isEmpty()) {
            System.err.println("Nenhuma UF encontrada.");
            return;
        }

        // Definir o número de colunas
        int columnNumber = 5;

        // Definir cores alternadas para as linhas
        String corBranca = "#FFFFFF"; // Branco
        String corCinza = "#F0F0F0"; // Cinza claro

        // Criar uma tabela para exibir os dados
        TableHandle table = designHandle.getElementFactory().newTableItem("UfTable", columnNumber);
        table.setWidth("100%");

        // Configurar as colunas da tabela
        for (int i = 0; i < columnNumber; i++) {
            ColumnHandle column = (ColumnHandle) table.getColumns().get(i);
            int columnWidth = 100 / columnNumber;
            column.setProperty("width", columnWidth + "%");
        }

        // Configurar o cabeçalho da tabela
        RowHandle header = (RowHandle) table.getHeader().get(0);

        // Adicionar células se não existirem
        while (header.getCells().getCount() < columnNumber) {
            header.getCells().add(designHandle.getElementFactory().newCell());
        }

        // Adicionar cada célula do cabeçalho
        addHeaderCell(designHandle, header, "ID", 0);
        addHeaderCell(designHandle, header, "Nome", 1);
        addHeaderCell(designHandle, header, "Sigla", 2);
        addHeaderCell(designHandle, header, "Código externo", 3);
        addHeaderCell(designHandle, header, "País", 4);

        // Adicionar linhas de detalhes com dados
        String currentRowColor = corBranca; // Começa com a cor branca
        for (UfDTO uf : ufs) {
            RowHandle detailRow = designHandle.getElementFactory().newTableRow();
            table.getDetail().add(detailRow);

            addDetailCell(designHandle, detailRow, uf.getCduf().toString(), currentRowColor);
            addDetailCell(designHandle, detailRow, uf.getDsuf(), currentRowColor);
            addDetailCell(designHandle, detailRow, uf.getSguf(), currentRowColor);
            addDetailCell(designHandle, detailRow, uf.getCodext() != null ? uf.getCodext().toString() : "", currentRowColor);
            addDetailCell(designHandle, detailRow, uf.getPais().getDspai(), currentRowColor);

            // Alternar a cor para a próxima linha
            currentRowColor = currentRowColor.equals(corBranca) ? corCinza : corBranca;
        }

        table.setProperty(StyleHandle.MARGIN_BOTTOM_PROP, "10px");

        // Adicionar a tabela ao corpo do relatório
        designHandle.getBody().add(table);
    }

    /**
     * Adicionar uma célula ao cabeçalho da tabela
     */
    private void addHeaderCell(ReportDesignHandle designHandle, RowHandle header, String content, int index) throws SemanticException {
        CellHandle cell = (CellHandle) header.getCells().get(index);
        TextItemHandle textItem = designHandle.getElementFactory().newTextItem(null);
        textItem.setContent(content);
        cell.getContent().add(textItem);
        header.setProperty(StyleHandle.FONT_WEIGHT_PROP, DesignChoiceConstants.FONT_WEIGHT_BOLD);
    }

    /**
     * Adicionar uma célula no corpo da tabela
     */
    private void addDetailCell(ReportDesignHandle designHandle, RowHandle detailRow, String content, String backgroundColor) throws SemanticException {
        CellHandle cell = designHandle.getElementFactory().newCell();
        TextItemHandle textItem = designHandle.getElementFactory().newTextItem(null);
        textItem.setContent(content);
        cell.getContent().add(textItem);
        detailRow.getCells().add(cell);
        detailRow.setProperty(StyleHandle.BACKGROUND_COLOR_PROP, backgroundColor);
        detailRow.setProperty(StyleHandle.TEXT_ALIGN_PROP, DesignChoiceConstants.TEXT_ALIGN_CENTER);
    }
}

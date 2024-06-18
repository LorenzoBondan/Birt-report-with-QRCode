package com.metaway.birt.designhandler;

import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.SharedStyleHandle;
import org.eclipse.birt.report.model.api.StyleHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.springframework.stereotype.Component;

/**
 * Esse arquivo contém os estilos para manipular o design dos elementos do relatório BIRT
 */
@Component
public class StylesHandler {

    /**
     * Estilo padrão criado para customização
     */
    public SharedStyleHandle getStandardStyle(ReportDesignHandle designHandle) throws SemanticException {
        SharedStyleHandle newStyle = designHandle.getElementFactory().newStyle("StandardStyle");
        newStyle.setProperty(StyleHandle.TEXT_ALIGN_PROP, DesignChoiceConstants.TEXT_ALIGN_CENTER);
        newStyle.setProperty(StyleHandle.FONT_SIZE_PROP, "12pt");
        newStyle.setProperty(StyleHandle.FONT_FAMILY_PROP, "Arial");
        newStyle.setProperty(StyleHandle.COLOR_PROP, "#333333");
        newStyle.setProperty(StyleHandle.BACKGROUND_COLOR_PROP, "#F0F0F0");
        newStyle.setProperty(StyleHandle.FONT_WEIGHT_PROP, DesignChoiceConstants.FONT_WEIGHT_NORMAL);
        newStyle.setProperty(StyleHandle.BORDER_BOTTOM_WIDTH_PROP, "1pt");
        newStyle.setProperty(StyleHandle.BORDER_BOTTOM_COLOR_PROP, "#CCCCCC");
        newStyle.setProperty(StyleHandle.BORDER_BOTTOM_STYLE_PROP, DesignChoiceConstants.LINE_STYLE_SOLID);
        designHandle.getStyles().add(newStyle);
        return newStyle;
    }

    /**
     * Buscar o estilo do título
     */
    public SharedStyleHandle getTitleStyle(ReportDesignHandle designHandle) throws SemanticException {
        SharedStyleHandle newStyle = designHandle.getElementFactory().newStyle("TitleStyle");
        newStyle.setProperty(StyleHandle.TEXT_ALIGN_PROP, DesignChoiceConstants.TEXT_ALIGN_CENTER);
        newStyle.setProperty(StyleHandle.FONT_SIZE_PROP, "18pt");
        newStyle.setProperty(StyleHandle.FONT_WEIGHT_PROP, DesignChoiceConstants.FONT_WEIGHT_BOLD);
        newStyle.setProperty(StyleHandle.BORDER_BOTTOM_WIDTH_PROP, "2pt");
        newStyle.setProperty(StyleHandle.BORDER_BOTTOM_COLOR_PROP, "#666666");
        newStyle.setProperty(StyleHandle.BORDER_BOTTOM_STYLE_PROP, DesignChoiceConstants.LINE_STYLE_SOLID);
        newStyle.setProperty(StyleHandle.MARGIN_BOTTOM_PROP, "20px");
        newStyle.setProperty(StyleHandle.PADDING_BOTTOM_PROP, "10px");
        newStyle.setProperty(StyleHandle.COLOR_PROP, "#0056b3");
        designHandle.getStyles().add(newStyle);
        return newStyle;
    }

    /**
     * Buscar o estilo do subtítulo
     */
    public SharedStyleHandle getSubtitleStyle(ReportDesignHandle designHandle) throws SemanticException {
        SharedStyleHandle newStyle = designHandle.getElementFactory().newStyle("SubtitleStyle");
        newStyle.setProperty(StyleHandle.TEXT_ALIGN_PROP, DesignChoiceConstants.TEXT_ALIGN_LEFT);
        newStyle.setProperty(StyleHandle.FONT_SIZE_PROP, "14pt");
        newStyle.setProperty(StyleHandle.FONT_WEIGHT_PROP, DesignChoiceConstants.FONT_WEIGHT_BOLD);
        newStyle.setProperty(StyleHandle.COLOR_PROP, "#444444");
        newStyle.setProperty(StyleHandle.MARGIN_BOTTOM_PROP, "15px");
        newStyle.setProperty(StyleHandle.PADDING_BOTTOM_PROP, "5px");
        designHandle.getStyles().add(newStyle);
        return newStyle;
    }

    /**
     * Estilo para textos comuns
     */
    public SharedStyleHandle getNormalTextStyle(ReportDesignHandle designHandle) throws SemanticException {
        SharedStyleHandle newStyle = designHandle.getElementFactory().newStyle("NormalTextStyle");
        newStyle.setProperty(StyleHandle.TEXT_ALIGN_PROP, DesignChoiceConstants.TEXT_ALIGN_LEFT);
        newStyle.setProperty(StyleHandle.FONT_SIZE_PROP, "10pt");
        newStyle.setProperty(StyleHandle.COLOR_PROP, "#333333");
        newStyle.setProperty(StyleHandle.MARGIN_BOTTOM_PROP, "10px");
        newStyle.setProperty(StyleHandle.PADDING_BOTTOM_PROP, "5px");
        designHandle.getStyles().add(newStyle);
        return newStyle;
    }
}

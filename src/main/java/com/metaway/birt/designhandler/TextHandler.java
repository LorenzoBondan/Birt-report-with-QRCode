package com.metaway.birt.designhandler;

import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.SharedStyleHandle;
import org.eclipse.birt.report.model.api.TextItemHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.springframework.stereotype.Component;

@Component
public class TextHandler {

    /**
     * Adicionar texto
     */
    public void addText(ReportDesignHandle designHandle, String text, SharedStyleHandle style) throws SemanticException {
        TextItemHandle title = designHandle.getElementFactory().newTextItem(null);
        title.setContentType(DesignChoiceConstants.TEXT_CONTENT_TYPE_HTML);
        title.setContent(text);
        title.setStyle(style);
        designHandle.getBody().add(title);
    }
}

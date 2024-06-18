package com.metaway.birt.designhandler;

import com.metaway.birt.entities.Relatorio;
import com.metaway.birt.services.PaisService;
import com.metaway.birt.services.UfService;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * Esse arquivo contém a lógica para manipular o design do relatório BIRT
 */
@Service
public class ReportDesignHandler {

	@Autowired
	private PaisService paisService;
	@Autowired
	private UfService ufService;

	@Autowired
	private ReportUtilsHandler reportUtilsHandler;
	@Autowired
	private StylesHandler stylesHandler;
	@Autowired
	private TextHandler textHandler;
	@Autowired
	private HeaderHandler headerHandler;
	@Autowired
	private FooterHandler footerHandler;
	@Autowired
	private ImageHandler imageHandler;
	@Autowired
	private TableHandler tableHandler;

	private static final Logger logger = LoggerFactory.getLogger(ReportDesignHandler.class);

	/**
	 * Método principal de geração do relatório
	 */
	public IReportRunnable manipulateReport(IReportRunnable design, IReportEngine engine, Relatorio relatorio) {
		try {
			// converte o arquivo XML em um objeto Java
			design = engine.openReportDesign("src/main/resources/reports" + File.separator + "new_report_1.rptdesign");

			// altera o conteúdo de uma label já existente
			reportUtilsHandler.changeLabelText(design, "labelExemplo", "textooooo");

			ReportDesignHandle reportDesignHandle = (ReportDesignHandle) design.getDesignHandle();

			// adiciona header, body e footer
			//reportUtilsHandler.clearBody(reportDesignHandle); // caso queira limpar os demais elementos contidos no design do arquivo
			headerHandler.addHeader(reportDesignHandle);
			footerHandler.addFooter(reportDesignHandle, relatorio);
			imageHandler.addImage(reportDesignHandle);

			textHandler.addText(reportDesignHandle, "Relatório de Países", stylesHandler.getTitleStyle(reportDesignHandle));
			textHandler.addText(reportDesignHandle,
					"Este relatório apresenta uma visão detalhada dos países, incluindo nomes e códigos.",
					stylesHandler.getSubtitleStyle(reportDesignHandle));

			tableHandler.addTable(reportDesignHandle, paisService.findAll(), "Tabela Países");

			textHandler.addText(reportDesignHandle, "Relatório de Unidades Federativas", stylesHandler.getTitleStyle(reportDesignHandle));
			textHandler.addText(reportDesignHandle,
					"Esta seção do relatório apresenta informações sobre as Unidades Federativas, incluindo nomes e códigos.",
					stylesHandler.getSubtitleStyle(reportDesignHandle));

			tableHandler.addTable(reportDesignHandle, ufService.findAll(), "Tabela UF");

			textHandler.addText(reportDesignHandle, "Conclusão",
					stylesHandler.getTitleStyle(reportDesignHandle));
			textHandler.addText(reportDesignHandle,
					"Este relatório foi gerado para fornecer uma visão abrangente dos países e suas respectivas Unidades Federativas. Para mais informações, entre em contato com nosso departamento de análises.",
					stylesHandler.getNormalTextStyle(reportDesignHandle));

			// registra o xml como array de bytes no objeto relatorio
			reportUtilsHandler.registerXmlAttributeAsByteArray(design, relatorio);

		} catch (EngineException | SemanticException | IOException | IllegalAccessException e) {
			logger.error("Erro ao manipular o relatório: ", e);
		}
        return design;
	}
}


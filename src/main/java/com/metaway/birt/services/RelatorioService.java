package com.metaway.birt.services;

import com.metaway.birt.designhandler.ReportDesignHandler;
import com.metaway.birt.dtos.RelatorioDTO;
import com.metaway.birt.entities.Relatorio;
import com.metaway.birt.repositories.RelatorioRepository;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;

/**
 * Esse arquivo configura e gerencia o mecanismo de relatórios BIRT,
 * cria uma tarefa para executar e renderizar o relatório, e salva a saída em um arquivo PDF.
 */
@Service
public class RelatorioService {

    @Autowired
    private RelatorioRepository repository;
    @Autowired
    private ReportDesignHandler reportDesignHandler;

    @Value("${FILE-NAME}")
    private String fileName;

    /**
     * Este é o método principal que configura o mecanismo BIRT e manipula o design do relatório
     */
    @Transactional
    public void runReport() {
        IReportEngine engine = null;
        EngineConfig config;

        Relatorio relatorio = criarRelatorio();

        try {
            config = new EngineConfig();
            Platform.startup(config);
            IReportEngineFactory factory = (IReportEngineFactory) Platform
                    .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
            engine = factory.createReportEngine(config);
            engine.changeLogLevel(Level.WARNING);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        IReportRunnable design = null;
        design = reportDesignHandler.manipulateReport(design, engine, relatorio); // geração do relatório

        IRunAndRenderTask task = engine.createRunAndRenderTask(design);

        PDFRenderOption pdfRenderOption = new PDFRenderOption();
        pdfRenderOption.setOutputFormat(PDFRenderOption.OUTPUT_FORMAT_PDF);
        File pdfFile = new File(fileName);
        pdfRenderOption.setOutputFileName(pdfFile.getAbsolutePath());
        task.setRenderOption(pdfRenderOption);

        relatorio.setContentType(pdfRenderOption.getOutputFormat());
        repository.save(relatorio);

        try {
            task.run();

            byte[] pdfContent = Files.readAllBytes(pdfFile.toPath());
            registerPdfAttributeAsByteArray(pdfContent, relatorio);
            calculateChecksum(pdfContent, relatorio);

        } catch (EngineException | IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            task.close();
            engine.shutdown();
            Platform.shutdown();
        }
    }

    /**
     * Cria o objeto Relatorio manipulado durante a execução do método
     */
    private Relatorio criarRelatorio(){
        Relatorio relatorio = new Relatorio();
        relatorio.setCriadoPor("Usuário admin");
        relatorio.setCriadoEm(LocalDateTime.now());
        repository.save(relatorio);
        return relatorio;
    }

    /**
     * Armazena o PDF gerado como um array de bytes no objeto Relatorio
     */
    public void registerPdfAttributeAsByteArray(byte[] pdfContent, Relatorio relatorio) throws IOException {
        relatorio.setPdf(pdfContent);
        repository.save(relatorio);
    }

    /**
     * Calcula e armazena o checksum do PDF gerado no objeto Relatorio
     */
    private void calculateChecksum(byte[] fileBytes, Relatorio relatorio) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(fileBytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        relatorio.setChecksum(sb.toString());
        repository.save(relatorio);
    }

    /**
     * Busca todos os relatórios
     */
    @Transactional(readOnly = true)
    public List<RelatorioDTO> findAll() {
        return repository.findAll().stream().map(RelatorioDTO::new).toList();
    }

    /**
     * Busca um relatório pelo seu Hash
     */
    @Transactional(readOnly = true)
    public RelatorioDTO findByHash(String hash) {
        return new RelatorioDTO(repository.findByHash(hash));
    }

    /**
     * Abre um arquivo salvo nos arrays de bytes do relatório por id
     * @param id = id do relatório
     * @param contentType = tipo de conteúdo (pdf ou xml)
     */
    public void openFile(Long id, String contentType) throws IOException {
        Relatorio relatorio = repository.findById(id).orElse(null);
        byte[] bytesFile;
        if(contentType.equals("pdf")) {
            bytesFile = relatorio.getPdf();
        } else if(contentType.equals("xml")) {
            bytesFile = relatorio.getXml();
        } else {
            throw new IOException("Tipo de conteúdo não encontrado: " + contentType);
        }

        // Criar um arquivo temporário para o PDF/XML
        File tempFile = File.createTempFile("relatorio_", "." + contentType); //
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(bytesFile);
        }

        // Abrir o arquivo no Windows
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", tempFile.getAbsolutePath()).start();
        } else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            new ProcessBuilder("open", tempFile.getAbsolutePath()).start();
        } else if (System.getProperty("os.name").toLowerCase().contains("nix") ||
                System.getProperty("os.name").toLowerCase().contains("nux")) {
            new ProcessBuilder("xdg-open", tempFile.getAbsolutePath()).start();
        } else {
            System.out.println("Abertura automática de arquivos não é suportada neste sistema.");
            System.out.println("Arquivo salvo em: " + tempFile.getAbsolutePath());
        }
    }
}
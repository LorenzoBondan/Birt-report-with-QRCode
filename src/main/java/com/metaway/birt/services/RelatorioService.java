package com.metaway.birt.services;

import com.metaway.birt.designhandler.ReportDesignHandler;
import com.metaway.birt.dtos.RelatorioDTO;
import com.metaway.birt.entities.Relatorio;
import com.metaway.birt.repositories.RelatorioRepository;
import com.metaway.birt.utils.CryptoUtils;
import com.metaway.birt.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    @Value("${SECRET-KEY}")
    private String secretKey;

    @Autowired
    private JwtUtils jwtUtils;

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

            // Preenche atributo pdf
            byte[] pdfContent = Files.readAllBytes(pdfFile.toPath());
            relatorio.setPdf(pdfContent);

            // Preenche atributo checksum
            String checksum = calculateChecksum(fileName);
            relatorio.setChecksum(checksum);

            // Criptografar e concatenar os dados com o checksum
            //encryptRelatorioData(relatorio);
            encryptRelatorioDataJwt(relatorio);
            repository.save(relatorio);

        } catch (Exception e) {
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
     * Calcula o checksum de um arquivo
     */
    private String calculateChecksum(String filePath) throws IOException, NoSuchAlgorithmException {
        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(fileBytes);

        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Criptografa os dados do relatório e substitui o hash
     * Método do CryptoUtils, que necessita de chave para decriptografar
     */
    private void encryptRelatorioData(Relatorio relatorio) throws Exception {
        SecretKey key = CryptoUtils.stringToKey(secretKey);
        String encryptedData = relatorio.getEncryptedData(key);
        String finalHash = encryptedData + "|" + relatorio.getChecksum();

        relatorio.setHash(finalHash);
        repository.save(relatorio);
    }

    /**
     * Criptografa os dados do relatório e substitui o hash
     * Método do JwtUtils, que não necessita de chave para decriptografar
     */
    private void encryptRelatorioDataJwt(Relatorio relatorio) {
        Map<String, Object> claims = relatorio.toClaims();
        String jwt = jwtUtils.createJwt(claims, 3600000); // 1 hora de validade
        String finalHash = jwt + "|" + relatorio.getChecksum();
        relatorio.setHash(finalHash);
    }

    /**
     * Compara o checksum do arquivo gerado com o checksum armazenado no banco de dados
     */
    public boolean compareChecksum(RelatorioDTO relatorio) throws IOException, NoSuchAlgorithmException {
        String currentChecksum = calculateChecksum(fileName);
        return currentChecksum.equals(relatorio.getChecksum());
    }

    /**
     * Decriptografa e verifica a hash de um um relatório pelo seu id
     * Decriptografa através do CryptoUtils, que necessita de chave
     * Utilizar ou esse ou o Jwt
     */
    public String decryptAndVerifyData(Long id) throws Exception {
        Relatorio relatorio = repository.findById(id).orElse(null);

        // Obter a chave secreta
        SecretKey key = CryptoUtils.stringToKey(secretKey);

        // Separar o hash e o checksum
        String[] parts = relatorio.getHash().split("\\|");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Formato de hash inválido");
        }
        String encryptedData = parts[0];

        // Decriptografar os dados
        String decryptedData = CryptoUtils.decrypt(encryptedData, key);

        // Validar os dados descriptografados
        String expectedData = "ID:" + relatorio.getCdrel() + "|Date:" + relatorio.getCriadoEm() + "|User:" + relatorio.getCriadoPor() + "|Checksum:" + relatorio.getChecksum();
        if (!decryptedData.equals(expectedData)) {
            throw new IllegalArgumentException("Os dados descriptografados não correspondem aos dados esperados");
        }

        // Retornar os dados descriptografados para verificação
        return decryptedData;
    }

    /**
     * Decriptografa e verifica a hash de um um relatório pelo seu id
     * Decriptografa através do JwtUtils, que não necessita de chave
     */
    public String decryptAndVerifyDataJwt(Long relatorioId) {
        Relatorio relatorio = repository.findById(relatorioId).orElse(null);

        String[] parts = relatorio.getHash().split("\\|");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Formato de hash inválido");
        }
        String jwt = parts[0];
        Claims claims = jwtUtils.parseJwt(jwt);

        Long id = ((Number) claims.get("id")).longValue();
        String date = (String) claims.get("date");
        String user = (String) claims.get("user");
        String checksum = (String) claims.get("checksum");

        String expectedData = "ID:" + id + ",Date:" + date + ",User:" + user + ",Checksum:" + checksum;
        String actualData = "ID:" + relatorio.getCdrel() + ",Date:" + relatorio.getCriadoEm() + ",User:" + relatorio.getCriadoPor() + ",Checksum:" + relatorio.getChecksum();

        if (!expectedData.equals(actualData)) {
            throw new IllegalArgumentException("Os dados descriptografados não correspondem aos dados esperados");
        }

        return actualData;
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
     * Busca um relatório pelo seu id
     */
    @Transactional(readOnly = true)
    public RelatorioDTO findById(Long id) {
        return new RelatorioDTO(Objects.requireNonNull(repository.findById(id).orElse(null)));
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
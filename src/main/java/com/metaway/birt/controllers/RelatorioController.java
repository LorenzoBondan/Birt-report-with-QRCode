package com.metaway.birt.controllers;

import com.metaway.birt.dtos.RelatorioDTO;
import com.metaway.birt.services.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/relatorios")
public class RelatorioController {

    @Autowired
    private RelatorioService service;

    @Value("${FILE-NAME}")
    private String fileName;

    @GetMapping("/generateReport")
    public ResponseEntity<FileSystemResource> generateReport() {
        service.runReport();
        File file = new File(fileName); // Caminho do arquivo PDF
        if (file.exists()) {
            FileSystemResource resource = new FileSystemResource(file);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } else {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<RelatorioDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping(value = "/{hash}")
    public ResponseEntity<RelatorioDTO> findByHash(@PathVariable("hash") String hash) {
        return ResponseEntity.ok(service.findByHash(hash));
    }

    @GetMapping(value = "/abrir/{id}")
    public ResponseEntity<?> openFile(@PathVariable("id") Long id, @RequestParam("contentType") String contentType) throws IOException {
        service.openFile(id, contentType);
        return ResponseEntity.ok().build();
    }
}
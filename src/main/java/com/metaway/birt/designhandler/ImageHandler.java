package com.metaway.birt.designhandler;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.metaway.birt.entities.Relatorio;
import org.eclipse.birt.report.model.api.ImageHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.StructureFactory;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.eclipse.birt.report.model.api.elements.structures.EmbeddedImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;

@Component
public class ImageHandler {

    @Value("${CAMINHO-URL}")
    private String CAMINHO_URL;

    private static final Logger logger = LoggerFactory.getLogger(ImageHandler.class);

    /**
     * Adicionar imagem no header
     */
    public ImageHandle addEmbeddedImage(ReportDesignHandle designHandle) throws IOException, SemanticException {
        // Verificar se a imagem já foi adicionada
        if (designHandle.findImage("myEmbeddedImage") != null) {
            // Imagem já foi adicionada, criar apenas o ImageHandle
            ImageHandle image = designHandle.getElementFactory().newImage(null);
            image.setSource(DesignChoiceConstants.IMAGE_REF_TYPE_EMBED);
            image.setImageName("myEmbeddedImage");
            image.setHeight("40px");
            image.setWidth("40px");
            return image;
        }

        // Obter o caminho da imagem usando o class loader
        URL imageUrl = getClass().getClassLoader().getResource("reports/image.png");

        if (imageUrl == null) {
            throw new RuntimeException("Imagem não encontrada no caminho especificado.");
        }

        // Carregar a imagem como um InputStream
        try (InputStream imageStream = imageUrl.openStream();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            byte[] imageChunk = new byte[1024];
            int bytesRead;

            while ((bytesRead = imageStream.read(imageChunk, 0, 1024)) != -1) {
                buffer.write(imageChunk, 0, bytesRead);
            }

            byte[] imageData = buffer.toByteArray();

            // Criar um EmbeddedImage
            EmbeddedImage embeddedImage = StructureFactory.createEmbeddedImage();
            embeddedImage.setName("myEmbeddedImage");
            embeddedImage.setType(DesignChoiceConstants.IMAGE_TYPE_IMAGE_PNG);
            embeddedImage.setData(imageData);

            // Adicionar a imagem embutida ao design
            designHandle.addImage(embeddedImage);

            // Criar um ImageHandle
            ImageHandle image = designHandle.getElementFactory().newImage(null);
            image.setSource(DesignChoiceConstants.IMAGE_REF_TYPE_EMBED);
            image.setImageName("myEmbeddedImage");
            image.setHeight("40px");
            image.setWidth("40px");

            return image;
        }
    }

    /**
     * Adicionar imagem no corpo do relatório
     */
    public void addImage(ReportDesignHandle designHandle){
        try {
            // Obter o caminho da imagem usando o class loader
            URL imageUrl = getClass().getClassLoader().getResource("reports/image.png");

            if (imageUrl == null) {
                throw new RuntimeException("Imagem não encontrada no caminho especificado.");
            }

            // Carregar a imagem como um InputStream
            try (InputStream imageStream = imageUrl.openStream();
                 ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

                byte[] imageChunk = new byte[1024];
                int bytesRead;

                while ((bytesRead = imageStream.read(imageChunk, 0, 1024)) != -1) {
                    buffer.write(imageChunk, 0, bytesRead);
                }

                byte[] imageData = buffer.toByteArray();

                // Criar uma imagem embutida
                EmbeddedImage embeddedImage = new EmbeddedImage();
                embeddedImage.setName("myEmbeddedImage_" + Instant.now());
                embeddedImage.setType(DesignChoiceConstants.IMAGE_TYPE_IMAGE_PNG);
                embeddedImage.setData(imageData);

                // Adicionar a imagem embutida ao design
                designHandle.addImage(embeddedImage);

                // Criar um ImageHandle
                ImageHandle image = designHandle.getElementFactory().newImage(null);
                image.setSource(DesignChoiceConstants.IMAGE_REF_TYPE_EMBED);
                image.setImageName("myEmbeddedImage");
                image.setHeight("100px");
                image.setWidth("100px");

                // Adicionar a imagem ao corpo do design
                designHandle.getBody().add(image);
            }

        } catch (Exception e) {
            logger.error("Error message", e);
        }
    }

    /**
     * Adicionar QR Code
     */
    public ImageHandle addQrCode(ReportDesignHandle designHandle, Relatorio relatorio) throws SemanticException, IOException {
        // Gerar QR code com as informações do relatório
        BufferedImage qrCodeImage = generateQrCode(CAMINHO_URL + "/" + relatorio.getHash()); // conteúdo do qr code

        // Converter BufferedImage para byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrCodeImage, "png", baos);
        byte[] imageData = baos.toByteArray();

        // Criar uma imagem embutida
        EmbeddedImage embeddedImage = new EmbeddedImage();
        embeddedImage.setName("qrCodeImage_" + Instant.now());
        embeddedImage.setType(DesignChoiceConstants.IMAGE_TYPE_IMAGE_PNG);
        embeddedImage.setData(imageData);

        // Adicionar a imagem embutida ao design
        designHandle.addImage(embeddedImage);

        // Criar um ImageHandle
        ImageHandle image = designHandle.getElementFactory().newImage(null);
        image.setSource(DesignChoiceConstants.IMAGE_REF_TYPE_EMBED);
        image.setImageName(embeddedImage.getName());
        image.setHeight("100px");
        image.setWidth("100px");

        return image;

        // Adicionar a imagem ao corpo do design (tornar void)
        //designHandle.getBody().add(image);
    }

    /**
     * Gerar QR Code
     */
    public BufferedImage generateQrCode(String text) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 200;
        int height = 200;
        try {
            return MatrixToImageWriter.toBufferedImage(qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height));
        } catch (WriterException e) {
            logger.error("Error message", e);
            return null;
        }
    }
}

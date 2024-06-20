package com.metaway.birt.entities;

import com.metaway.birt.utils.CryptoUtils;
import jakarta.persistence.*;
import lombok.*;

import javax.crypto.SecretKey;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "cdrel", callSuper = false)
@Entity
@Table(name = "relatorio", schema = "public")
public class Relatorio extends AuditoriaInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cdrel;
    @Column(unique = true, columnDefinition = "TEXT")
    private String hash;
    @Column(name = "content_type")
    private String contentType;
    private String checksum;
    private byte[] xml;
    private byte[] pdf;

    @PrePersist
    protected void onCreate() {
        if (this.hash == null || this.hash.isEmpty()) {
            this.hash = UUID.randomUUID().toString();
        }
    }

    @Override
    public String toString() {
        return "Relatorio{" +
                "cdrel=" + cdrel +
                ", hash='" + hash + '\'' +
                ", " + super.toString() +
                '}';
    }

    /**
     * CryptoUtils
     */
    public String getEncryptedData(SecretKey key) throws Exception {
        String data = "ID:" + this.cdrel + "|Date:" + this.getCriadoEm().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                + "|User:" + this.getCriadoPor() + "|Checksum:" + this.checksum;
        return CryptoUtils.encrypt(data, key);
    }

    /**
     * JwtUtils
     * Monta o payload
     */
    public Map<String, Object> toClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", this.cdrel);
        claims.put("date", this.getCriadoEm().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        claims.put("user", this.getCriadoPor());
        claims.put("checksum", this.checksum);
        return claims;
    }
}

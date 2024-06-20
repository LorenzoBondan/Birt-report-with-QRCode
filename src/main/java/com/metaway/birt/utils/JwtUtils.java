package com.metaway.birt.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * Utilizar ou esse ou o CryptoUtils
 * Esse é aberto, e pode ser decriptografado sem chave
 */
@Component
public class JwtUtils {

    @Value("${JWT-SECRET}")
    private String jwtSecret;

    private SecretKey key;

    @PostConstruct
    public void init() {
        // Decodifica a chave da variável de ambiente
        byte[] decodedKey = Base64.getDecoder().decode(jwtSecret);
        // Cria uma chave secreta usando a chave decodificada
        this.key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
    }

    public String createJwt(Map<String, Object> claims, long expirationMillis) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseJwt(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    /*
    public static void main(String[] args) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[32]; // 32 bytes = 256 bits
        secureRandom.nextBytes(key);
        String base64Key = Base64.getEncoder().withoutPadding().encodeToString(key);
        System.out.println(base64Key);
    }
    */
}

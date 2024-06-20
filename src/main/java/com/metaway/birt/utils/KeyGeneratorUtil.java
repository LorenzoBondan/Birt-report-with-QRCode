package com.metaway.birt.utils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

/**
 * Gera uma chave de segurança para ser utilizada em conjunto com o CryptoUtils
 */
public class KeyGeneratorUtil {

    private static final String ALGORITHM = "AES";

    public static String generateAndStoreKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(128);
        SecretKey key = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static void main(String[] args) throws Exception {
        String key = generateAndStoreKey();
        System.out.println("Generated Key (store this securely): " + key);
    }
}

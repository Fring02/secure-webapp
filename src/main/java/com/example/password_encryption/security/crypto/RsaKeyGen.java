package com.example.password_encryption.security.crypto;

import com.example.password_encryption.util.JwtUtilService;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Component
public class RsaKeyGen {
    private KeyFactory keyFactory;
    private final Logger logger = LoggerFactory.getLogger(RsaKeyGen.class);
    private KeyPair keyPair;
    private final String keyStoragePath = System.getProperty("user.dir");
    public RsaKeyGen() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
            keyFactory = KeyFactory.getInstance("RSA");
            logger.info("Initialized key pair generator with key size 2048");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    public PublicKey getPublicKey(JwtUtilService jwtUtilService, String token) throws InvalidKeySpecException, DecoderException {
        String publicKeyStr = jwtUtilService.getAllClaimsFromToken(token).get("public_key").toString();
        try {
            byte[] publicKeyBytes = Hex.decodeHex(publicKeyStr.toCharArray());
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            return keyFactory.generatePublic(publicKeySpec);
        } catch (InvalidKeySpecException | DecoderException e) {
            logger.error("Failed to retrieve public key, {}", e.getMessage());
            throw e;
        }
    }
    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }
    public PrivateKey getPrivateKey(long userId) throws IOException, InvalidKeySpecException {
        File privateKeyFile = new File(String.format("%s\\keys\\%d_rsa_private.key", keyStoragePath, userId));
        try {
            byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            return keyFactory.generatePrivate(privateKeySpec);
        } catch (IOException | InvalidKeySpecException e) {
            logger.error("Failed to retrieve private key, {}", e.getMessage());
            throw e;
        }
    }
    public void saveOrRewritePrivateKey(long userId) throws IOException {
        var privateKey = keyPair.getPrivate();
        try (OutputStream fos = new FileOutputStream(String.format("%s\\keys\\%d_rsa_private.key", keyStoragePath, userId))) {
            fos.write(privateKey.getEncoded());
        } catch (IOException e) {
            logger.error("Failed to store private key, {}", e.getMessage());
            throw e;
        }
    }
}

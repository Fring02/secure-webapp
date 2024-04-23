package com.example.password_encryption.crypto.encrypt;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.security.*;
import javax.crypto.*;
import java.util.Base64;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.*;

@RestController
public class TextEncrypt {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public TextEncrypt() {
        try {
            // Generate Key Pair
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            this.publicKey = keyPair.getPublic();
            this.privateKey = keyPair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/encrypt")
    public byte[] encrypt(@RequestParam("file") MultipartFile file) {
        try {
            // Generate a symmetric key
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            SecretKey secretKey = keyGenerator.generateKey();

            // Encrypt the file with the symmetric key
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedFileBytes = cipher.doFinal(file.getBytes());

            // Encrypt the symmetric key with RSA public key
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedKey = cipher.doFinal(secretKey.getEncoded());

            // Concatenate encrypted key and encrypted file bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(encryptedKey);
            outputStream.write(encryptedFileBytes);
            return outputStream.toByteArray();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("/decrypt")
    public byte[] decrypt(@RequestBody byte[] encryptedData) {
        try {
            // Split encrypted data into encrypted key and encrypted file bytes
            int keySize = 256; // assuming 2048-bit RSA key size
            byte[] encryptedKey = new byte[keySize / 8];
            byte[] encryptedFileBytes = new byte[encryptedData.length - encryptedKey.length];
            System.arraycopy(encryptedData, 0, encryptedKey, 0, encryptedKey.length);
            System.arraycopy(encryptedData, encryptedKey.length, encryptedFileBytes, 0, encryptedFileBytes.length);

            // Decrypt the symmetric key with RSA private key
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedKeyBytes = cipher.doFinal(encryptedKey);
            SecretKey secretKey = new SecretKeySpec(decryptedKeyBytes, "AES");

            // Decrypt the file with the symmetric key
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(encryptedFileBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }
}

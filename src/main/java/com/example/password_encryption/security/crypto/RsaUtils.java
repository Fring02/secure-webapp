package com.example.password_encryption.security.crypto;

import com.example.password_encryption.util.EncryptionException;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RsaUtils {
    public static byte[] encrypt(byte[] data, PublicKey publicKey) throws EncryptionException {
        try {
            /*Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return encryptCipher.doFinal(data);*/

            // Generate a symmetric key
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            SecretKey secretKey = keyGenerator.generateKey();
            // Encrypt the file with the symmetric key
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedFileBytes = cipher.doFinal(data);
            // Encrypt the symmetric key with RSA public key
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedKey = cipher.doFinal(secretKey.getEncoded());
            // Concatenate encrypted key and encrypted file bytes
            byte[] result = new byte[encryptedKey.length + encryptedFileBytes.length];
            System.arraycopy(encryptedKey, 0, result, 0, encryptedKey.length);
            System.arraycopy(encryptedFileBytes, 0, result, encryptedKey.length, encryptedFileBytes.length);
            return result;
        } catch (IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException
                 | InvalidKeyException e) {
            throw new EncryptionException("Failed to encrypt file: " + e.getMessage());
        }
    }
    public static byte[] decrypt(byte[] encryptedData, PrivateKey privateKey) throws EncryptionException {
        try {
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
        } catch (IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException
                 | InvalidKeyException e) {
            throw new EncryptionException("Failed to decrypt file: " + e.getMessage());
        }
    }
}

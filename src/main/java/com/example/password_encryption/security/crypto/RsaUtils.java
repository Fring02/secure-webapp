package com.example.password_encryption.security.crypto;

import com.example.password_encryption.util.EncryptionException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RsaUtils {
    public static byte[] encrypt(byte[] data, PublicKey publicKey) throws EncryptionException {
        try {
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return encryptCipher.doFinal(data);
        } catch (IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException
                 | InvalidKeyException e) {
            throw new EncryptionException("Failed to encrypt file: " + e.getMessage());
        }
    }
    public static byte[] decrypt(byte[] data, PrivateKey privateKey) throws EncryptionException {
        try {
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
            return encryptCipher.doFinal(data);
        } catch (IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException
                 | InvalidKeyException e) {
            throw new EncryptionException("Failed to decrypt file: " + e.getMessage());
        }
    }
}

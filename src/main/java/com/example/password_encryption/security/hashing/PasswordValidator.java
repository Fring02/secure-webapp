package com.example.password_encryption.security.hashing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
@Service
public class PasswordValidator {
    @Value("${pbkdf2.iterations.count}")
    private int iterations;
    @Value("${pbkdf2.algorithm}")
    private String algorithm;
    public boolean verify(String password, byte[] hash, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(algorithm);
        byte[] hashToVerify = skf.generateSecret(spec).getEncoded();

        int diff = hash.length ^ hashToVerify.length;
        for(int i = 0; i < hash.length && i < hashToVerify.length; i++)
            diff |= hash[i] ^ hashToVerify[i];
        return diff == 0;
    }
}

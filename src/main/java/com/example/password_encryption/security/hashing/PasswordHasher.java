package com.example.password_encryption.security.hashing;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

@Service
public class PasswordHasher {
    @Value("${pbkdf2.iterations.count}")
    private int iterations;
    @Value("${pbkdf2.keysize}")
    private int keySize;
    @Value("${pbkdf2.algorithm}")
    private String algorithm;
    public byte[] generateSalt(){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[32];
        random.nextBytes(salt);
        return salt;
    }
    public byte[] generateHash(String data, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(data.toCharArray(), salt, iterations, keySize);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(algorithm);

        byte[] hash = skf.generateSecret(spec).getEncoded();
        return hash;
    }
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
    public String toHex(byte[] arr){
        return Hex.encodeHexString(arr);
    }
}

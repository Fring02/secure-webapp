package com.example.password_encryption;

import org.junit.jupiter.api.Test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HMACTests {
    @Test
    public void givenDataAndKeyAndAlgorithm_whenHmacWithJava_thenSuccess()
            throws NoSuchAlgorithmException, InvalidKeyException {

        String hmacSHA256Value = "5b50d80c7dc7ae8bb1b1433cc0b99ecd2ac8397a555c6f75cb8a619ae35a0c35";
        String hmacSHA256Algorithm = "HmacSHA256";
        String data = "baeldung";
        String key = "123456";

        // String result = HMACUtils.generateHMAC(hmacSHA256Algorithm, data, key);

        // assertEquals(hmacSHA256Value, result);
    }
}

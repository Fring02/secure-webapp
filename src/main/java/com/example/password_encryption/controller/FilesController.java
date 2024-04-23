package com.example.password_encryption.controller;

import com.example.password_encryption.model.File;
import com.example.password_encryption.service.FilesService;
import com.example.password_encryption.util.EncryptionException;
import com.example.password_encryption.util.JwtUtilService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequestMapping("/api/files")
public class FilesController extends BaseController {
    private final FilesService filesService;
    protected FilesController(JwtUtilService jwtUtilService, FilesService filesService) {
        super(LoggerFactory.getLogger(FilesController.class), jwtUtilService);
        this.filesService = filesService;
    }

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String accessToken)
            throws IOException, DecoderException, EncryptionException, InvalidKeySpecException {
        accessToken = validateAndFetchToken(accessToken);
        String username = jwtService.getUsernameFromToken(accessToken);
        filesService.upload(file, accessToken);
        return ResponseEntity.ok().body("Uploaded");
    }
    @GetMapping("/{id}")
    public HttpEntity<byte[]> downloadFile(@PathVariable long id, @RequestHeader("Authorization") String accessToken)
            throws IOException, IllegalAccessException, EncryptionException, InvalidKeySpecException {
        accessToken = validateAndFetchToken(accessToken);
        String username = jwtService.getUsernameFromToken(accessToken);
        File file = filesService.download(id, username);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_PDF);
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName()
                .substring(0, file.getName().lastIndexOf(".pdf")).replace(" ", "_"));
        header.setContentLength(file.getContent().length);
        return new HttpEntity<>(file.getContent(), header);
    }


    /*@PostMapping
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
    }*/
}

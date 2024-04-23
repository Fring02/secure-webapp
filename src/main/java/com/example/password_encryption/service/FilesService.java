package com.example.password_encryption.service;

import com.example.password_encryption.data.FilesRepository;
import com.example.password_encryption.data.UsersRepository;
import com.example.password_encryption.model.File;
import com.example.password_encryption.model.User;
import com.example.password_encryption.security.crypto.RsaKeyGen;
import com.example.password_encryption.security.crypto.RsaUtils;
import com.example.password_encryption.util.EncryptionException;
import com.example.password_encryption.util.JwtUtilService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.codec.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

@Service
public class FilesService {
    private final FilesRepository repository;
    private final UsersRepository usersRepository;
    private final JwtUtilService jwtUtilService;
    private final Logger logger = LoggerFactory.getLogger(FilesService.class);
    public FilesService(FilesRepository repository, UsersRepository usersRepository, JwtUtilService jwtUtilService) {
        this.repository = repository;
        this.usersRepository = usersRepository;
        this.jwtUtilService = jwtUtilService;
    }
    public void upload(MultipartFile file, String token) throws IOException, DecoderException, InvalidKeySpecException, EncryptionException {
        if(!file.getContentType().endsWith("pdf")) throw new IllegalArgumentException("File format is invalid. Only pdf is accepted");
        String username = jwtUtilService.getUsernameFromToken(token);
        var userOpt = usersRepository.findByUsername(username);
        if(userOpt.isEmpty()) throw new EntityNotFoundException("User with username " + username + " doesn't exist");
        if(repository.existsByName(file.getName())) throw new EntityExistsException("File with such name already exists");
        var fileBytes = file.getBytes();
        var fileName = file.getOriginalFilename() + ".pdf";

        RsaKeyGen rsaKeyGen = new RsaKeyGen();
        PublicKey publicKey = rsaKeyGen.getPublicKey(jwtUtilService, token);
        fileBytes = RsaUtils.encrypt(fileBytes, publicKey);

        File newFile = new File(fileName, userOpt.get().getId(), fileBytes);
        repository.save(newFile);
        logger.info("Saved file " + fileName + " for user " + userOpt.get().getId());
    }
    public File download(long fileId, String username) throws IllegalAccessException, IOException, InvalidKeySpecException, EncryptionException {
        var userOpt = usersRepository.findByUsername(username);
        if(userOpt.isEmpty()) throw new EntityNotFoundException("User with username " + username + " doesn't exist");
        Optional<File> fileOpt = repository.findById(fileId);
        if(fileOpt.isEmpty()) throw new EntityNotFoundException("File doesn't exist");
        File file = fileOpt.get();
        User user = userOpt.get();
        if(file.getUserId() != user.getId()) throw new IllegalAccessException("Unauthorized file download attempt");
        RsaKeyGen rsaKeyGen = new RsaKeyGen();
        PrivateKey privateKey = rsaKeyGen.getPrivateKey(user.getId());
        byte[] data = RsaUtils.decrypt(file.getContent(), privateKey);
        file.setContent(data);
        return file;
    }
}

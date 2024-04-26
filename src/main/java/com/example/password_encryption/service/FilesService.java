package com.example.password_encryption.service;

import com.example.password_encryption.data.FilesRepository;
import com.example.password_encryption.data.UsersRepository;
import com.example.password_encryption.dto.FileDto;
import com.example.password_encryption.model.File;
import com.example.password_encryption.model.User;
import com.example.password_encryption.security.crypto.RsaKeyGen;
import com.example.password_encryption.security.crypto.RsaUtils;
import com.example.password_encryption.util.EncryptionException;
import com.example.password_encryption.util.JwtUtilService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.List;
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
    public void upload(MultipartFile file, String token, long id) throws IOException, EncryptionException {
        if(!file.getContentType().endsWith("pdf")) throw new IllegalArgumentException("File format is invalid. Only pdf is accepted");
        long userId = jwtUtilService.getUserIdFromToken(token);
        var userOpt = usersRepository.findById(userId);
        if(userOpt.isEmpty()) throw new EntityNotFoundException("User with id " + userId + " doesn't exist");
        if(repository.existsByName(file.getName())) throw new EntityExistsException("File with such name already exists");
        var fileBytes = file.getBytes();
        var fileName = file.getOriginalFilename();

        File newFile = new File(fileName, userOpt.get().getId(), fileBytes);
        newFile = repository.save(newFile);
        var fileId = newFile.getId();

        RsaKeyGen keyGenerator = new RsaKeyGen();
        PublicKey publicKey = keyGenerator.getPublicKey();
        keyGenerator.savePrivateKey(id, fileId);
        fileBytes = RsaUtils.encrypt(fileBytes, publicKey);
        newFile.setContent(fileBytes);
        newFile = repository.save(newFile);

        logger.info("Saved file " + fileName + " for user " + userOpt.get().getId());
    }
    public File download(long fileId, long userId) throws IllegalAccessException, IOException, InvalidKeySpecException, EncryptionException {
        var userOpt = usersRepository.findById(userId);
        if(userOpt.isEmpty()) throw new EntityNotFoundException("User with id " + userId + " doesn't exist");
        Optional<File> fileOpt = repository.findById(fileId);
        if(fileOpt.isEmpty()) throw new EntityNotFoundException("File doesn't exist");
        File file = fileOpt.get();
        User user = userOpt.get();
        if(file.getUserId() != user.getId()) throw new IllegalAccessException("Unauthorized file download attempt");
        RsaKeyGen rsaKeyGen = new RsaKeyGen();
        PrivateKey privateKey = rsaKeyGen.getPrivateKey(user.getId(), file.getId());
        byte[] data = RsaUtils.decrypt(file.getContent(), privateKey);
        file.setContent(data);
        return file;
    }
    public List<FileDto> getByUserId(long userId){
        return repository.findByUserId(userId);
    }
}

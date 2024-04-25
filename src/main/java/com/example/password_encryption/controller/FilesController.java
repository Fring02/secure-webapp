package com.example.password_encryption.controller;

import com.example.password_encryption.model.File;
import com.example.password_encryption.service.FilesService;
import com.example.password_encryption.util.EncryptionException;
import com.example.password_encryption.util.JwtUtilService;
import org.apache.commons.codec.DecoderException;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
            throws IOException, EncryptionException {
        accessToken = validateAndFetchToken(accessToken);
        var id = jwtService.getUserIdFromToken(accessToken);
        filesService.upload(file, accessToken, id);
        return ResponseEntity.ok().body("Uploaded");
    }
    @GetMapping("/{file_id}")
    public HttpEntity<byte[]> downloadFile(@PathVariable("file_id") long id, @RequestHeader("Authorization") String accessToken)
            throws IOException, IllegalAccessException, EncryptionException, InvalidKeySpecException {
        accessToken = validateAndFetchToken(accessToken);
        long userId = jwtService.getUserIdFromToken(accessToken);
        File file = filesService.download(id, userId);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_PDF);
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName()
                .substring(0, file.getName().lastIndexOf(".pdf")).replace(" ", "_"));
        header.setContentLength(file.getContent().length);
        return new HttpEntity<>(file.getContent(), header);
    }

    @GetMapping
    public ResponseEntity<?> getFilesForUser(@RequestParam(name = "userId") long userId,
                                             @RequestHeader("Authorization") String accessToken){
        accessToken = validateAndFetchToken(accessToken);
        long id = jwtService.getUserIdFromToken(accessToken);
        if(id != userId) return ResponseEntity.status(401).body("Unauthorized access");
        var files = filesService.getByUserId(id);
        if(files.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(files);
    }
}

package com.shoppingmall.controller;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    private Path uploadDirPath;

    @PostConstruct
    public void init() {
        try {
            uploadDirPath = Paths.get(uploadPath).toAbsolutePath();
            if (!Files.exists(uploadDirPath)) {
                Files.createDirectories(uploadDirPath);
            }
            log.info("Upload directory: {}", uploadDirPath);
        } catch (IOException e) {
            log.error("Failed to create upload directory", e);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "File is empty"));
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String newFilename = UUID.randomUUID().toString() + extension;
            Path filePath = uploadDirPath.resolve(newFilename);
            file.transferTo(filePath.toFile());
            
            String fileUrl = "/api/upload/" + newFilename;
            result.put("success", true);
            result.put("url", fileUrl);
            result.put("filename", newFilename);
            
            log.info("File uploaded: {}", fileUrl);
            return ResponseEntity.ok(result);
            
        } catch (IOException e) {
            log.error("Upload failed", e);
            result.put("success", false);
            result.put("message", "Upload failed: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    @GetMapping("/{filename}")
    public ResponseEntity<byte[]> getFile(@PathVariable String filename) {
        try {
            Path filePath = uploadDirPath.resolve(filename);
            if (Files.exists(filePath)) {
                byte[] data = Files.readAllBytes(filePath);
                String contentType = Files.probeContentType(filePath);
                return ResponseEntity.ok()
                        .header("Content-Type", contentType != null ? contentType : "application/octet-stream")
                        .body(data);
            }
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }
}
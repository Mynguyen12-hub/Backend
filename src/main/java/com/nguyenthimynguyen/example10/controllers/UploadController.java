package com.nguyenthimynguyen.example10.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @PostMapping("/images")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Lấy thư mục hiện tại (thư mục project)
            String projectPath = System.getProperty("user.dir");
            String uploadDirPath = projectPath + File.separator + "uploads" + File.separator + "images";
            
            Path uploadDir = Paths.get(uploadDirPath);
            
            // Tạo thư mục nếu chưa tồn tại
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Lưu file
            String fileName = file.getOriginalFilename();
            Path filePath = uploadDir.resolve(fileName);
            Files.write(filePath, file.getBytes());

            // Trả về đường dẫn để frontend truy cập
            String fileUrl = "/images/" + fileName;
            System.out.println("✅ Upload thành công: " + fileUrl + " -> " + filePath.toAbsolutePath());
            return ResponseEntity.ok(fileUrl);
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi upload: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Upload failed! Error: " + e.getMessage());
        }
    }
}

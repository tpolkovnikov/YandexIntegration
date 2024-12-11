package com.example.backend;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.nio.file.Path;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class MyController {
    @GetMapping("/api/hello")
    public String sayHello() {
        return "Hello from backend!";
    }


    @GetMapping("/api/toYandex")
    public String Yandex() {

        return "Hello from backend!";
    }


    @PostMapping("/save-token")
    public ResponseEntity<String> saveToken(@RequestBody TokenRequest tokenRequest) {
        System.out.println("Получен токен: " + tokenRequest.getToken());
        // Сохраните токен в базе данных или временно в памяти
        return ResponseEntity.ok("Токен сохранен");
    }

    public static class TokenRequest {
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
    

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        System.out.println("Получен запрос на загрузку файла: " + file.getOriginalFilename());
        try {
            
            // Получение пути относительно корневой папки приложения
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "src", "uploads");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Папка uploads была создана.");
            }
    
            
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            System.out.println("Путь для сохранения файла: " + filePath.toAbsolutePath());
    
            file.transferTo(filePath.toFile());
            System.out.println("Файл успешно сохранен: " + filePath.toString());
    
            return ResponseEntity.ok("Файл успешно загружен: " + fileName);
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке файла: " + e.getMessage());
            return ResponseEntity.status(500).body("Ошибка при загрузке файла: " + e.getMessage());
        }
    }
}

package com.example.backend;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.Path;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.YandexClasses.Disk;
import com.example.backend.YandexClasses.YandexDiskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.backend.TokenReader ; 


import org.springframework.http.*;



@RestController
public class MyController {

    static TokenReader temp = new TokenReader();

    // тут хранится OAuth-токен 
    String token;

    
    public static class TokenRequest {
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    // сохранение токена
    @PostMapping("/api/save-token")
    public ResponseEntity<String> saveToken(@RequestBody TokenRequest tokenRequest) {
        // Сохраните токен в базе данных или временно в памяти
        token = tokenRequest.getToken();

        System.out.println("Получен токен: " + token);

        // Указываем путь к JSON-файлу
        Path jsonFilePath = Paths.get("token.json");
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Создаем объект для записи в JSON
            TokenReader.OAuthToken oauthToken = new TokenReader.OAuthToken();
            oauthToken.setOauthToken(token);

            // Если файл не существует, создаем его
            if (!Files.exists(jsonFilePath)) {
                Files.createFile(jsonFilePath);
            }

            // Сериализуем объект в JSON и записываем в файл
            objectMapper.writeValue(jsonFilePath.toFile(), oauthToken);

            System.out.println("Токен успешно сохранен в JSON-файл: " + jsonFilePath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Ошибка при записи токена в JSON-файл: " + e.getMessage());
            return ResponseEntity.status(500).body("Ошибка при сохранении токена");
        }

        return ResponseEntity.ok("Токен сохранен");
    }

    // загрузка на бек в папку
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

    // запрос о диске
    @GetMapping("/YandexDisk")
    public ResponseEntity<?> YandexDisk() {

        try {
            token = temp.getOAuthToken("token.json");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(400).body("Токен не найден");
        }
        
        YandexDiskService diskService = new YandexDiskService();
        try {   
            // Получаем данные о диске
            Disk diskData = diskService.getDiskData(token);

            // Логируем данные о диске (опционально)
            System.out.println("Общее пространство: " + diskData.getTotalSpace());
            System.out.println("Использованное пространство: " + diskData.getUsedSpace());
            System.out.println("Папка приложений: " + diskData.getSystemFolders().getApplications());
            System.out.println("Папка загрузок: " + diskData.getSystemFolders().getDownloads());

            // Возвращаем данные о диске в формате JSON
            return ResponseEntity.ok(diskData); // Возвращаем объект Disk в формате JSON

        } catch (Exception e) {
            // Обработка ошибок
            System.err.println("Не удалось получить данные о диске: " + e.getMessage());
            return ResponseEntity.status(500).body("Не удалось получить данные о диске: " + e.getMessage());
        }
    }
}

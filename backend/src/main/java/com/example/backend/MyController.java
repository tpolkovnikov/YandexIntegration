package com.example.backend;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.nio.file.Path;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.YandexClasses.Disk;
import com.example.backend.YandexClasses.YandexAppFolder;
import com.example.backend.YandexClasses.YandexCreateFolder;
import com.example.backend.YandexClasses.YandexDownload;
import com.example.backend.YandexClasses.YandexLoading;
import com.example.backend.YandexClasses.YandexDiskFiles;
import com.example.backend.YandexClasses.YandexDiskService;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

import com.example.backend.TokenReader ;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

import java.io.*;
import java.net.URL;

@RestController
public class MyController {

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

    @GetMapping("/api/files")
    public List<String> getUploadedFiles() {
        Path UPLOAD_DIR = Paths.get(System.getProperty("user.dir"), "src", "uploads");
        File dir = UPLOAD_DIR.toFile();
        File[] files = dir.listFiles();
        List<String> fileNames = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }
        }
        return fileNames;
    }

    @DeleteMapping("api/files/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        Path filePath = Paths.get(System.getProperty("user.dir"), "src", "uploads", fileName);

        try {
            // Проверка, существует ли файл
            if (Files.exists(filePath)) {
                // Удаление файла
                Files.delete(filePath);
                return ResponseEntity.ok("Файл успешно удален");
            } else {
                return ResponseEntity.status(404).body("Файл не найден");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Ошибка при удалении файла");
        }
    }

    // запрос о диске
    @GetMapping("/Yandex/Disk")
    public ResponseEntity<?> YandexDisk() {

        try {
            token = TokenReader.getOAuthToken("token.json");
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

    @GetMapping("/yandex/files")
    public Mono<ResponseEntity<String>> getFiles(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String mediaType,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) String fields,
            @RequestParam(required = false) String previewSize,
            @RequestParam(required = false) Boolean previewCrop) throws IOException{

        YandexDiskFiles yandexDiskFiles = new YandexDiskFiles();
        String token = TokenReader.getOAuthToken("token.json");

        return yandexDiskFiles.fetchFiles(token, limit, mediaType, offset, fields, previewSize, previewCrop)
                .map(response -> ResponseEntity.ok().body(response))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(500).body("Error: " + e.getMessage())));
    }

    // получение файлов из папки приложения
    @GetMapping("/yandex/appfiles")
    public String getAppFolderFiles() throws IOException {
        YandexAppFolder yandexAppFolder = new YandexAppFolder();
        String token = TokenReader.getOAuthToken("token.json");
        return yandexAppFolder.getAppFolderFiles(token);
    }

    // создание папки
    @PostMapping("yandex/folder/create")
    public String createFolder(@RequestParam String folderName) throws IOException {
        YandexCreateFolder yandexCreateFolder = new YandexCreateFolder();
        String token = TokenReader.getOAuthToken("token.json");
        return yandexCreateFolder.createFolder(token, folderName);
    }

    
    @PostMapping("yandex/download")
    public String downloadFile(@RequestParam String filePath) {
        YandexDownload yandexDonwload = new YandexDownload();
        try {
            yandexDonwload.downloadFile(filePath);
            return "Файл успешно загружен!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при загрузке файла: " + e.getMessage();
        }
    }

    @PostMapping("/yandex/upload")
    public String loadingFile(@RequestParam String filePath) {
        YandexLoading yandexLoading = new YandexLoading();
        try {
            yandexLoading.loadingFile(filePath);
            return "Файл успешно загружен!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при загрузке файла: " + e.getMessage();
        }
    }


}

package com.example.backend;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.backend.YandexClasses.Disk;
import com.fasterxml.jackson.annotation.JsonProperty;

import reactor.core.publisher.Mono;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;


// потом объединить всё в 1 класс
public class YandexClasses {


    public static String generateYandexDownloadLink(String filePath) {
        // Заменяем символы, которые должны остаться в пути
        String encodedPath = filePath.replace("/", "%2F");
        // Формируем правильный URL
        return encodedPath;
    }

    // для получения данных о диске
    // сделалти статическим - чтобы спокойно создавать экземпляры в других файлах (так как является вложенным классом (класс в классе))
    public static class YandexDiskService {

        private final WebClient webClient;

    public YandexDiskService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://cloud-api.yandex.net/v1/disk")
                .build();
    }

    public Disk getDiskData(String token) {
        try {
            return webClient.get()
                    .uri("/") // Конечная точка для получения данных о диске
                    .header("Authorization", "OAuth " + token)
                    .retrieve()
                    .bodyToMono(Disk.class) // Указываем класс для десериализации
                    .block();
        } catch (WebClientResponseException e) {
            System.err.println("Ошибка: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
            throw e;
        }
    }
    }


    // получение списка файлов
    public static class YandexDiskFiles {

        private final WebClient webClient;

        public YandexDiskFiles() {
            this.webClient = WebClient.builder()
                .baseUrl("https://cloud-api.yandex.net/v1/disk/resources/files")
                .build();
        }

        public Mono<String> fetchFiles(String token, Integer limit, String mediaType, Integer offset, String fields, String previewSize, Boolean previewCrop) {
            try {
                return webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .queryParam("limit", limit)
                                .queryParam("media_type", mediaType)
                                .queryParam("offset", offset)
                                .queryParam("fields", fields)
                                .queryParam("preview_size", previewSize)
                                .queryParam("preview_crop", previewCrop)
                                .build())
                        .header("Authorization", "OAuth " + token)
                        .retrieve()
                        .bodyToMono(String.class);
            } catch (WebClientResponseException e) {
                System.err.println("Ошибка: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
                throw e;
            }
        }
    }

    public static class YandexAppFolder {

        private final WebClient webClient;

        public YandexAppFolder() {
            this.webClient = WebClient.builder()
            .baseUrl("https://cloud-api.yandex.net")
            .build();
        }

        public String getAppFolderFiles(String token) {
            System.err.println("i tyt1");
            String url = UriComponentsBuilder.fromUriString("/v1/disk/resources")
                    .queryParam("path", "/GreenData")
                    .toUriString();

            try {
                System.err.println("i tyt2");

                return webClient.get()
                        .uri(url)
                        .header("Authorization", "OAuth " + token)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
            } catch (WebClientResponseException e) {
                System.err.println("i tyt3");

                System.err.println("Ошибка: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
                throw e;
            }
        }
    }


    public static class YandexCreateFolder {

        private final WebClient webClient;
    
        public YandexCreateFolder() {
            this.webClient = WebClient.builder()
            .baseUrl("https://cloud-api.yandex.net")
            .build();
        }
    
        public String createFolder(String token, String folderName) {
            String encodedPath = UriComponentsBuilder.fromPath("/v1/disk/resources")
                    .queryParam("path", folderName)
                    .build()
                    .encode()
                    .toUriString();
    
            return webClient.put()
                    .uri(encodedPath)
                    .header("Authorization", "OAuth " + token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }
    }

    public static class YandexDownload {

        private final WebClient webClient;

        public YandexDownload() {
            this.webClient = WebClient.builder()
                    .baseUrl("https://cloud-api.yandex.net/v1/disk")
                    .build();
        }

        public void downloadFile(String filePath) throws IOException {
            // Получение OAuth токена
            String token = TokenReader.getOAuthToken("token.json");
            
            // Закодировать путь, но оставить символ '%' нетронутым
            //String encodedPath = filePath.replace("%25", "%"); // Заменяем только проценты
            String encodedFilePath = URLEncoder.encode(filePath, "UTF-8");

            System.out.println(encodedFilePath);

            // Формирование запроса для получения ссылки на скачивание
            String downloadUrl = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/resources/download")
                        .queryParam("path", filePath)
                        .build())
                    .header("Authorization", "OAuth " + token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(response -> {
                        // Извлечение ссылки из JSON-ответа
                        int urlStart = response.indexOf("\"href\":\"") + 8;
                        int urlEnd = response.indexOf("\"", urlStart);
                        String url = response.substring(urlStart, urlEnd);
                        try {
                            // Декодируем URL, чтобы устранить двойное кодирование
                            return URLDecoder.decode(url, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException("Ошибка при декодировании URL", e);
                        }
                    })
                    .block();

            if (downloadUrl == null || downloadUrl.isEmpty()) {
                throw new IOException("Не удалось получить ссылку на скачивание");
            }
            
            System.out.println("tyt1");
            System.out.println("Download URL: " + downloadUrl);

            //Далее идёт скачивание по ссылке 

            try {
                // Декодирование URL
                String decodedUrl = java.net.URLDecoder.decode(downloadUrl, "UTF-8");

                // Получаем имя файла из параметра filename в URL
                String fileName = extractFileNameFromUrl(decodedUrl);
                if (fileName == null || fileName.isEmpty()) {
                    System.err.println("Unable to determine file name from URL.");
                    return;
                }
                //String fileName = "Zombatar_1.jpg"; // Имя файла
    
                // Папка для сохранения
                Path saveDir = Paths.get("D:/Education/repository/YandexIntegration/backend/src/uploads");
                Path pfilePath = saveDir.resolve(fileName);
    
                // Создаем папку, если она не существует
                Files.createDirectories(saveDir);
    
                // Инициализация подключения
                URL url = new URL(decodedUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
    
                // Проверка кода ответа
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    System.out.println("Connection successful. Starting file download...");
    
                    // Скачиваем файл
                    try (InputStream inputStream = connection.getInputStream();
                         FileOutputStream outputStream = new FileOutputStream(pfilePath.toFile())) {
    
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
    
                        System.out.println("File saved successfully to: " + pfilePath);
                    }
                } else {
                    System.err.println("Failed to download file. HTTP Response Code: " + responseCode);
                }
    
                connection.disconnect();
    
            } catch (Exception e) {
                System.err.println("Error occurred: " + e.getMessage());
                e.printStackTrace();
            }    
        }
    }
    
    // Извлечение имени файла из URL, если оно присутствует в параметре 'filename'
    private static String extractFileNameFromUrl(String url) {
        Pattern pattern = Pattern.compile("filename=([^&]*)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null; // Если имя файла не найдено в URL
    }

    // Извлечение имени файла из заголовка Content-Disposition
    private static String getFileNameFromContentDisposition(HttpURLConnection connection) {
        String contentDisposition = connection.getHeaderField("Content-Disposition");
        if (contentDisposition != null && contentDisposition.contains("filename=")) {
            int index = contentDisposition.indexOf("filename=");
            if (index > 0) {
                return contentDisposition.substring(index + 9).replaceAll("\"", "");
            }
        }
        return null; // Если имя файла не найдено в заголовке
    }

    

    public static class YandexLoading{

        private final WebClient webClient;

        public YandexLoading() {
            this.webClient = WebClient.builder()
                .baseUrl("https://cloud-api.yandex.net/v1/disk")
                .build();
        }   
            
            
        // filePath - имя загружаемого файла
        public void loadingFile(String filePath) throws IOException {
            // Получение OAuth токена
            String token = TokenReader.getOAuthToken("token.json");
            
            // Закодировать путь, но оставить символ '%' нетронутым
            //String encodedPath = filePath.replace("%25", "%"); // Заменяем только проценты
            String encodedFilePath = URLEncoder.encode(filePath, "UTF-8");

            System.out.println(encodedFilePath);

            // Формирование запроса для получения ссылки на скачивание
            String loadingUrl = webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/resources/upload")
                    .queryParam("path", "GreenData/" + filePath)
                    .build())
                    .header("Authorization", "OAuth " + token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(response -> {
                    // Извлечение ссылки из JSON-ответа
                    int urlStart = response.indexOf("\"href\":\"") + 8;
                    int urlEnd = response.indexOf("\"", urlStart);
                    String url = response.substring(urlStart, urlEnd);
                    try {
                        // Декодируем URL, чтобы устранить двойное кодирование
                        return URLDecoder.decode(url, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException("Ошибка при декодировании URL", e);
                    }
                })
                .block();

            if (loadingUrl == null || loadingUrl.isEmpty()) {
                throw new IOException("Не удалось получить ссылку на загрузка");
            }

            //System.out.println(loadingUrl);
            
            try {
                // Получаем файл из папки src/uploads
                File file = new File("src/uploads/" + filePath);
                if (!file.exists()) {
                    System.out.println("ошибка файл не найден: " + filePath);
                }

                // Читаем содержимое файла в байты
                byte[] fileContent = Files.readAllBytes(file.toPath());

                // Отправляем файл на указанный URL с использованием PUT
                ResponseEntity<Void> response = webClient.put()
                    .uri(loadingUrl)
                    .bodyValue(fileContent) // Отправляем содержимое файла
                    .retrieve()
                    .toBodilessEntity()
                    .block();

                // Проверяем успешность загрузки
                if (response != null && response.getStatusCode() == HttpStatus.CREATED) {
                    System.out.println("sucsess: " + filePath);
                } else {
                    System.out.println("no sucsess: " + filePath);
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("IOException: " + filePath);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception: " + filePath);
            }
        }

    }



    // класс для десериализации json-ответа
    public static class Disk {

        @JsonProperty("trash_size")
        private long trashSize;

        @JsonProperty("total_space")
        private long totalSpace;

        @JsonProperty("used_space")
        private long usedSpace;

        @JsonProperty("system_folders")
        private SystemFolders systemFolders;

        // Геттеры и сеттеры
        public long getTrashSize() {
            return trashSize;
        }

        public void setTrashSize(long trashSize) {
            this.trashSize = trashSize;
        }

        public long getTotalSpace() {
            return totalSpace;
        }

        public void setTotalSpace(long totalSpace) {
            this.totalSpace = totalSpace;
        }

        public long getUsedSpace() {
            return usedSpace;
        }

        public void setUsedSpace(long usedSpace) {
            this.usedSpace = usedSpace;
        }

        public SystemFolders getSystemFolders() {
            return systemFolders;
        }

        public void setSystemFolders(SystemFolders systemFolders) {
            this.systemFolders = systemFolders;
        }

        public static class SystemFolders {
        private String applications;
        private String downloads;

        // Геттеры и сеттеры
        public String getApplications() {
            return applications;
        }

        public void setApplications(String applications) {
            this.applications = applications;
        }

        public String getDownloads() {
            return downloads;
        }

        public void setDownloads(String downloads) {
            this.downloads = downloads;
        }
    }
    }   

    // класс для обработки ответа скачивания
    public class YandexDownloadResponse {
        private String href;
        private String method;
        private boolean templated;
    
        public String getHref() {
            return href;
        }
    
        public void setHref(String href) {
            this.href = href;
        }
    
        public String getMethod() {
            return method;
        }
    
        public void setMethod(String method) {
            this.method = method;
        }
    
        public boolean isTemplated() {
            return templated;
        }
    
        public void setTemplated(boolean templated) {
            this.templated = templated;
        }
    }



}

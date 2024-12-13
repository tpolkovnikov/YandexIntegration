package com.example.backend;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

import reactor.core.publisher.Mono;


// потом объединить всё в 1 класс
public class YandexClasses {

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




}

package com.example.backend;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.annotation.JsonProperty;

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

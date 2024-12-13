package com.example.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class TokenReader {

    public static String getOAuthToken(String jsonFilePath) throws IOException {
        // Создаем ObjectMapper для работы с JSON
        ObjectMapper objectMapper = new ObjectMapper();

        // Читаем JSON-файл в Map или в DTO-класс
        OAuthToken tokenObject = objectMapper.readValue(new File(jsonFilePath), OAuthToken.class);

        // Возвращаем значение токена
        return tokenObject.getOauthToken();
    }

    // DTO-класс для JSON-файла
    public static class OAuthToken {
        public String oauth_token;

        public String getOauthToken() {
            return oauth_token;
        }

        public void setOauthToken(String oauth_token) {
            this.oauth_token = oauth_token;
        }
    }
}

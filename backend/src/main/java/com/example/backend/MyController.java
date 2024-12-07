package com.example.backend;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}

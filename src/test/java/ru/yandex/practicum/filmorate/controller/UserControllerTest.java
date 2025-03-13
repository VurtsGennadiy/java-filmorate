package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.filmorate.FilmorateApplication;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserControllerTest {
    HttpClient client;
    ConfigurableApplicationContext applicationContext;
    URI uri = URI.create("http://localhost:8080/users");
    String userJson = "{" +
            "\"email\":\"practicum@yandex.ru\"," +
            "\"login\":\"student\"," +
            "\"name\":\"Имя Фамилия\"," +
            "\"birthday\":\"1990-01-01\"" +
            "}";


    @BeforeEach
    void setUp() {
        applicationContext = SpringApplication.run(FilmorateApplication.class);
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        SpringApplication.exit(applicationContext);
    }

    @Test
    void getAllUsers() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void createUser() throws IOException, InterruptedException {
        String expected = "{" +
                "\"id\":1," +
                "\"email\":\"practicum@yandex.ru\"," +
                "\"login\":\"student\"," +
                "\"name\":\"Имя Фамилия\"," +
                "\"birthday\":\"1990-01-01\"" +
                "}";

        HttpRequest request = HttpRequest.newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .setHeader("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(expected, response.body());
    }

    @Test
    void updateUser() throws IOException, InterruptedException {
        HttpRequest requestPost = HttpRequest.newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .setHeader("Content-Type", "application/json")
                .build();
        client.send(requestPost, HttpResponse.BodyHandlers.ofString());

        String updUserJson = "{" +
                "\"id\":1," +
                "\"email\":\"practicum@yandex.ru\"," +
                "\"login\":\"tester\"," +
                "\"name\":\"Имя Фамилия\"," +
                "\"birthday\":\"1990-01-01\"" +
                "}";

        HttpRequest requestPut = HttpRequest.newBuilder(uri)
                .PUT(HttpRequest.BodyPublishers.ofString(updUserJson))
                .setHeader("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(requestPut, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(updUserJson, response.body());
    }

    @Test
    void emailDuplicate() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .setHeader("Content-Type", "application/json")
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    void notFound() throws IOException, InterruptedException {
        String updUserJson = "{" +
                "\"id\":1," +
                "\"email\":\"practicum@yandex.ru\"," +
                "\"login\":\"tester\"," +
                "\"name\":\"Имя Фамилия\"," +
                "\"birthday\":\"1990-01-01\"" +
                "}";

        HttpRequest requestPut = HttpRequest.newBuilder(uri)
                .PUT(HttpRequest.BodyPublishers.ofString(updUserJson))
                .setHeader("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(requestPut, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}

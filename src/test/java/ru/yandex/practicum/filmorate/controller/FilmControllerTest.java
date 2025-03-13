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


public class FilmControllerTest {
    ConfigurableApplicationContext applicationContext;
    HttpClient client;
    URI uri = URI.create("http://localhost:8080/films");
    String filmJson = "{" +
            "\"name\":\"Собачье сердце\"," +
            "\"description\":\"Профессор Преображенский превращает уличного пса в человека.\"," +
            "\"releaseDate\":\"1988-11-20\"," +
            "\"duration\":8160" +
            "}";

    @BeforeEach
    void setUp() {
        client = HttpClient.newHttpClient();
        applicationContext = SpringApplication.run(FilmorateApplication.class);
    }

    @AfterEach
    void tearDown() {
        SpringApplication.exit(applicationContext);
    }

    @Test
    void getAllFilms() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void createFilm() throws IOException, InterruptedException {
        String expectedJson = "{" +
                "\"id\":1," +
                "\"name\":\"Собачье сердце\"," +
                "\"description\":\"Профессор Преображенский превращает уличного пса в человека.\"," +
                "\"releaseDate\":\"1988-11-20\"," +
                "\"duration\":8160" +
                "}";

        HttpRequest request = HttpRequest.newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .setHeader("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(expectedJson, response.body());
    }

    @Test
    void updateFilm() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(filmJson))
                .setHeader("Content-Type", "application/json")
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        String updFilmJson = "{" +
                "\"id\":1," +
                "\"name\":\"День сурка\"," +
                "\"description\":\"Телевизионный комментатор Фил Коннорс каждый год приезжает в маленький городок" +
                " в штате Пенсильвания на празднование Дня сурка.\"," +
                "\"releaseDate\":\"1993-02-02\"," +
                "\"duration\":6060" +
                "}";

        HttpRequest requestUpdate = HttpRequest.newBuilder(uri)
                .PUT(HttpRequest.BodyPublishers.ofString(updFilmJson))
                .setHeader("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(updFilmJson, response.body());
    }

    @Test
    void notFound() throws IOException, InterruptedException {
        String updFilmJson = "{" +
                "\"id\":1," +
                "\"name\":\"День сурка\"," +
                "\"description\":\"Телевизионный комментатор Фил Коннорс каждый год приезжает в маленький городок" +
                " в штате Пенсильвания на празднование Дня сурка.\"," +
                "\"releaseDate\":\"1993-02-02\"," +
                "\"duration\":6060" +
                "}";

        HttpRequest requestUpdate = HttpRequest.newBuilder(uri)
                .PUT(HttpRequest.BodyPublishers.ofString(updFilmJson))
                .setHeader("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}

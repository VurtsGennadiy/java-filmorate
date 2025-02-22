package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    private Integer id;
    private String name;
    private String description;
    LocalDate releaseDate;
    Duration duration;
}

package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import ru.yandex.practicum.filmorate.annotation.SinceDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    public static final String CINEMA_DAY = "1895-12-28";
    private Integer id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @PastOrPresent
    @SinceDate(CINEMA_DAY)
    private LocalDate releaseDate;

    @Positive
    private Integer duration;
}

package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import ru.yandex.practicum.filmorate.annotation.AfterCinemaDay;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    private Integer id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @PastOrPresent
    @AfterCinemaDay(message = "должно быть не раньше 28 декабря 1895 г.")
    LocalDate releaseDate;

    @Positive
    Integer duration;
}

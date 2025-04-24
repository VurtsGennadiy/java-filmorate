package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.*;

@Component
public class InMemoryGenreStorage implements GenreStorage {
    private final Map<Integer, Genre> genres;
    public InMemoryGenreStorage() {
        genres = new HashMap<>();
        genres.put(1, new Genre(1,"Комедия"));
        genres.put(2, new Genre(2,"Драма"));
        genres.put(3, new Genre(3,"Мультфильм"));
        genres.put(4, new Genre(4,"Триллер"));
        genres.put(5, new Genre(5,"Документальный"));
        genres.put(6, new Genre(6,"Боевик"));
    }
    @Override
    public List<Genre> findAll() {
        return List.copyOf(genres.values());
    }

    @Override
    public Optional<Genre> findById(int id) {
        return Optional.ofNullable(genres.get(id));
    }

    @Override
    public boolean containsAll(Collection<Integer> ids) {
        return genres.keySet().containsAll(ids);
    }
}

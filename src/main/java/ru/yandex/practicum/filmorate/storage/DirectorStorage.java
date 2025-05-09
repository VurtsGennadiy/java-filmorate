package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import java.util.Optional;
import java.util.Collection;

public interface DirectorStorage {
    Director create(Director director);

    Director update(Director director);

    void remove(int directorId);

    Optional<Director> getDirector(int directorId);

    Collection<Director> getDirectors();

    boolean containsAll(Collection<Integer> ids);
}

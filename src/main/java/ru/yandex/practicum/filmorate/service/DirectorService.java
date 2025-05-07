package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.db.DirectorRepository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorRepository directorRepository;

    public Director getDirector(int id) {
        return directorRepository.getDirector(id)
                .orElseThrow(() -> new NotFoundException("Режиссёр с id = " + id + " не существует."));
    }

    public Collection<Director> getDirectors() {
        return directorRepository.getDirectors();
    }

    public Director create(Director director) {
        return directorRepository.create(director);
    }

    public Director update(Director director) {
        checkDirectorExists(director.getId());
        return directorRepository.update(director);
    }

    public void remove(int directorId) {
        checkDirectorExists(directorId);
        directorRepository.remove(directorId);
    }

    private void checkDirectorExists(int directorId) {
        directorRepository.getDirector(directorId)
                .orElseThrow(() -> new NotFoundException("Режиссёр с id = " + directorId + " не существует."));
    }
}

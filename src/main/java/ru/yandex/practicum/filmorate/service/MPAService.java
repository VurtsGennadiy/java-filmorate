package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MPAService {
    private final MPAStorage storage;

    public List<MPA> getAll() {
        return storage.findAll();
    }

    public MPA getMPAbyId(int id) {
        return storage.findById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг MPA с id = " + id + " не найден"));
    }
}

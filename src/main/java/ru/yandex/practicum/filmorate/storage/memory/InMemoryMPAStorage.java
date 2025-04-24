package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryMPAStorage implements MPAStorage {
    private final Map<Integer, MPA> MPARatings;

    public InMemoryMPAStorage() {
        MPARatings = new HashMap<>();
        MPARatings.put(1, new MPA(1,"G"));
        MPARatings.put(2, new MPA(2,"PG"));
        MPARatings.put(3, new MPA(3,"PG-13"));
        MPARatings.put(4, new MPA(4,"R"));
        MPARatings.put(5, new MPA(5,"NC-17"));
    }

    @Override
    public List<MPA> findAll() {
        return List.copyOf(MPARatings.values());
    }

    @Override
    public Optional<MPA> findById(int id) {
        return Optional.ofNullable(MPARatings.get(id));
    }
}

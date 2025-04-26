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
    private final Map<Integer, MPA> mpaRatings;

    public InMemoryMPAStorage() {
        mpaRatings = new HashMap<>();
        mpaRatings.put(1, new MPA(1,"G"));
        mpaRatings.put(2, new MPA(2,"PG"));
        mpaRatings.put(3, new MPA(3,"PG-13"));
        mpaRatings.put(4, new MPA(4,"R"));
        mpaRatings.put(5, new MPA(5,"NC-17"));
    }

    @Override
    public List<MPA> getAll() {
        return List.copyOf(mpaRatings.values());
    }

    @Override
    public Optional<MPA> getById(int id) {
        return Optional.ofNullable(mpaRatings.get(id));
    }
}

package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.MPAService;

import java.util.List;

@RestController()
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Validated
public class MPAController {
    private final MPAService service;

    @GetMapping
    public List<MPA> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MPA getById(@PathVariable @Positive int id) {
        return service.getMPAbyId(id);
    }
}

package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;


import java.util.Map;

public interface FilmStorage {


    Map<Integer, Film> getFilms();

    int getID();
}

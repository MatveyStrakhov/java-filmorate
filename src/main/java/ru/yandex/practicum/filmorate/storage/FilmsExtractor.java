package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j

public class FilmsExtractor implements ResultSetExtractor<List<Film>> {
    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Film> data = new ArrayList<>();
        Map<Integer, Film> films = new LinkedHashMap<>();
        Map<Integer, Set<Genre>> genres = new HashMap<>();
        Map<Integer, Set<Director>> directors = new HashMap<>();
        while (rs.next()) {
            Film film = Film.builder()
                    .id(rs.getInt("id"))
                    .duration(rs.getInt("duration"))
                    .name(rs.getString("name"))
                    .mpa(Rating.builder().id(rs.getInt("rating_id")).name(rs.getString("rating_name")).build())
                    .description(rs.getString("description"))
                    .releaseDate(rs.getDate("release_date").toLocalDate())
                    .build();
            Integer genreId = rs.getInt("genre_id");
            String genreName = rs.getString("genre");
            log.info("genre found: " + genreName);
            Genre genre = new Genre(genreId, genreName);
            Set<Genre> genresOfFilm = new HashSet<>();
            Integer directorId = rs.getInt("director_id");
            String directorName = rs.getString("director");
            Director director = new Director(directorId, directorName);
            Set<Director> directorsOfFilm = new HashSet<>();
            films.put(film.getId(), film);
            genres.putIfAbsent(film.getId(), genresOfFilm);
            if ((genreId != 0) || (genreName != null)) {
                genres.get(film.getId()).add(genre);
            }
            directors.putIfAbsent(film.getId(), directorsOfFilm);
            if ((directorId != 0) || (directorName != null)) {
                directors.get(film.getId()).add(director);
            }
        }

        for (Integer id : films.keySet()) {
            films.get(id).setGenres(genres.get(id).stream().sorted(Comparator.comparing(Genre::getId, Integer::compareTo)).collect(Collectors.toCollection(LinkedHashSet::new)));
            films.get(id).setDirectors(directors.get(id).stream().sorted(Comparator.comparing(Director::getDirectorId, Integer::compareTo)).collect(Collectors.toCollection(LinkedHashSet::new)));

            data.add(films.get(id));
        }
        return data;

    }
}

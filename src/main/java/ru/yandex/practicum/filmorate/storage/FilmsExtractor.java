package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
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
        Map<Integer, Film> films = new HashMap<>();
        Map<Integer, Set<Genre>> genres = new HashMap<>();
        while (rs.next()) {
            Film film = Film.builder()
                    .id(rs.getInt("id"))
                    .duration(rs.getInt("duration"))
                    .name(rs.getString("name"))
                    .mpa(Rating.builder().id(rs.getInt("rating_id")).name(rs.getString("rating_name")).build())
                    .description(rs.getString("description"))
                    .releaseDate(rs.getDate("release_date").toLocalDate())
                    .build();
            try {
                Integer genreId = rs.getInt("genre_id");
                String genreName = rs.getString("genre");
                log.info("genre found: " + genreName);
                Genre genre = new Genre(genreId, genreName);
                if ((genreId == 0) || (genreName == null)) {
                    throw new SQLException("No genres for this film!");
                }
                if (!films.containsKey(film.getId())) {
                    films.put(film.getId(), film);
                    Set<Genre> genresOfFilm = new HashSet<>();
                    genresOfFilm.add(genre);
                    genres.put(film.getId(), genresOfFilm);
                } else {
                    genres.get(film.getId()).add(genre);
                }
            } catch (SQLException e) {
                if (!films.containsKey(film.getId())) {
                    films.put(film.getId(), film);
                    Set<Genre> genresOfFilm = new HashSet<>();
                    genres.put(film.getId(), genresOfFilm);
                }
            }

        }

        for (Integer id : films.keySet()) {
            films.get(id).setGenres(genres.get(id).stream().sorted(Comparator.comparing(Genre::getId, Integer::compareTo)).collect(Collectors.toCollection(LinkedHashSet::new)));

            data.add(films.get(id));
        }
        return data;
    }
}

package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmControllerTest {

    private final ObjectMapper objectMapper;

    private final MockMvc mockMvc;

    @Test
    void shouldReturn200ForGETFilms() throws Exception {
        mockMvc.perform(get("/films")).andExpect(status().is2xxSuccessful());
    }

    @Test
    void shouldReturn200AndSameFilmWhenPOSTFilms() throws Exception {
        Film film1 = Film.builder()
                .name("kobayashi")
                .description("abs")
                .duration(1)
                .releaseDate(LocalDate.of(2021, 1, 21))
                .mpa(Rating.builder().id(1).name("G").build())
                .build();
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.description").value("abs"))
                .andExpect(jsonPath("$.duration").value("1"))
                .andExpect(jsonPath("$.releaseDate").value("2021-01-21"));
    }

    @Test
    void shouldReturn500WhenPOSTFilmsAndNameIsBlank() throws Exception {
        Film film1 = Film.builder()
                .name(" ")
                .description("szx")
                .duration(1)
                .mpa(Rating.builder().id(1).name("G").build())
                .releaseDate(LocalDate.of(2021, 1, 21))
                .build();
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn500WhenDescriptionIsLongerThen200ForPOSTFilms() throws Exception {
        Film film1 = Film.builder()
                .name("random")
                .description("szxromwvnzqdbaorjpxcxkppssvnzjawgfjqycaoayuuygvispeygxxmjbvqifvas" +
                        "ecdxizdmhwzsnayidimpulhlqamnsdnvngidohmgsquqixonxlkauvsnraahnmtzg" +
                        "oeslftgjmabiqziufqcohfzetvnugvoibnvjuxtzylxholziqfbabviomgvhdlmeve" +
                        "qxqernxaqwcyujqxfccqyesaydpkdvxfuvrdoeniivxqamgykwwsgcteauoiylbqladcwajvrsdqs" +
                        "qttcxvyoohbtxowlhsmflnzshlzjgaweizafahddqxmvyvzcafkfrjipdfjdgwfolnydykkpkbwszanmnf")
                .duration(1)
                .releaseDate(LocalDate.of(2021, 1, 21))
                .mpa(Rating.builder().id(1).name("G").build())
                .build();
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    void shouldReturn500WhenDateIsTooEarlyForPOSTFilms() throws Exception {
        Film film1 = Film.builder()
                .name("random")
                .description("szx")
                .duration(1)
                .releaseDate(LocalDate.of(1895, 12, 27))
                .mpa(Rating.builder().id(1).name("G").build())
                .build();
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    void shouldReturn500WhenDurationIsNegativeForPOSTFilms() throws Exception {
        Film film1 = Film.builder()
                .name("random")
                .description("szx")
                .duration(-1)
                .releaseDate(LocalDate.of(1995, 12, 27))
                .mpa(Rating.builder().id(1).name("G").build())
                .build();
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    void shouldReturn500WhenIdIsWrongForPUTFilms() throws Exception {
        Film film1 = Film.builder()
                .id(-1)
                .name("random")
                .description("szx")
                .mpa(Rating.builder().id(1).name("G").build())
                .duration(1)
                .releaseDate(LocalDate.of(1995, 12, 27))
                .build();
        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(film1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldReturn200AndSameFilmWhenPUTFilms() throws Exception {
        Film film1 = Film.builder()
                .name("kobayashi")
                .description("abs")
                .duration(1)
                .releaseDate(LocalDate.of(2021, 1, 21))
                .mpa(Rating.builder().id(1).name("G").build())
                .build();
        Film film2 = Film.builder()
                .id(1)
                .name("kobayashi")
                .description("abse")
                .duration(1)
                .mpa(Rating.builder().id(1).name("G").build())
                .releaseDate(LocalDate.of(2021, 1, 21))
                .build();
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film1))
                        .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(film2))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.description").value("abse"))
                .andExpect(jsonPath("$.duration").value("1"))
                .andExpect(jsonPath("$.releaseDate").value("2021-01-21"));
    }

    @Test
    void shouldReturn500WhenPUTFilmsAndNameIsBlank() throws Exception {
        Film film1 = Film.builder()
                .name("kobayashi")
                .description("szx")
                .duration(1)
                .releaseDate(LocalDate.of(2021, 1, 21))
                .mpa(Rating.builder().id(1).name("G").build())
                .build();
        Film film2 = Film.builder()
                .id(1)
                .name("  ")
                .description("abs")
                .duration(1)
                .releaseDate(LocalDate.of(2021, 1, 21))
                .mpa(Rating.builder().id(1).name("G").build())
                .build();
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film1))
                        .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(film2))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    void shouldReturn500WhenDescriptionIsLongerThen200ForPUTFilms() throws Exception {
        Film film1 = Film.builder()
                .name("random")
                .description("szx")
                .duration(1)
                .releaseDate(LocalDate.of(2021, 1, 21))
                .mpa(Rating.builder().id(1).name("G").build())
                .build();
        Film film2 = Film.builder()
                .id(1)
                .name("random")
                .description("szxromwvnzqdbaorjpxcxkppssvnzjawgfjqycaoayuuygvispeygxxmjbvqifvas" +
                        "ecdxizdmhwzsnayidimpulhlqamnsdnvngidohmgsquqixonxlkauvsnraahnmtzg" +
                        "oeslftgjmabiqziufqcohfzetvnugvoibnvjuxtzylxholziqfbabviomgvhdlmeve" +
                        "qxqernxaqwcyujqxfccqyesaydpkdvxfuvrdoeniivxqamgykwwsgcteauoiylbqladcwajvrsdqs" +
                        "qttcxvyoohbtxowlhsmflnzshlzjgaweizafahddqxmvyvzcafkfrjipdfjdgwfolnydykkpkbwszanmnf")
                .duration(1)
                .mpa(Rating.builder().id(1).name("G").build())
                .releaseDate(LocalDate.of(2021, 1, 21))
                .build();

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film1))
                        .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(film2))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    void shouldReturn500WhenDateIsTooEarlyForPUTFilms() throws Exception {
        Film film1 = Film.builder()
                .name("random")
                .description("szx")
                .duration(1)
                .releaseDate(LocalDate.of(1995, 12, 27))
                .mpa(Rating.builder().id(1).name("G").build())
                .build();
        Film film2 = Film.builder()
                .name("random")
                .description("szx")
                .duration(1)
                .releaseDate(LocalDate.of(1895, 12, 27))
                .mpa(Rating.builder().id(1).name("G").build())
                .build();
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film1))
                        .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(film2))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());


    }

    @Test
    void shouldReturn500WhenDurationIsNegativeForPUTFilms() throws Exception {
        Film film1 = Film.builder()
                .name("random")
                .description("szx")
                .duration(1)
                .releaseDate(LocalDate.of(1995, 12, 27))
                .mpa(Rating.builder().id(1).name("G").build())
                .build();
        Film film2 = Film.builder()
                .name("random")
                .description("szx")
                .duration(-1)
                .releaseDate(LocalDate.of(1995, 12, 27))
                .mpa(Rating.builder().id(1).name("G").build())
                .build();
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film1))
                        .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(film2))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnListOfFilmsByDirectorSortedByYear() throws Exception {
        Director director = Director.builder()
                .directorId(1)
                .directorName("New Director")
                .build();
        Set<Director> directors = new HashSet<>();
        directors.add(director);
        Film film1 = Film.builder()
                .name("random1")
                .description("szx1")
                .duration(1)
                .directors(directors)
                .releaseDate(LocalDate.of(1995, 12, 27))
                .mpa(Rating.builder().id(1).name("G").build())
                .build();
        Film film2 = Film.builder()
                .name("random2")
                .description("szx2")
                .directors(directors)
                .duration(1)
                .releaseDate(LocalDate.of(1996, 12, 27))
                .mpa(Rating.builder().id(1).name("G").build())
                .build();
        mockMvc.perform(
                        post("/directors")
                                .content(objectMapper.writeValueAsString(director))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film2))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        mockMvc.perform(get("/films/director/1?sortBy=year"))
                .andExpect(status().is2xxSuccessful());

    }

}

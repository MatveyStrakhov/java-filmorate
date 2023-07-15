package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Test
    void shouldReturn200ForGETFilms() throws Exception {
        mockMvc.perform(get("/films")).andExpect(status().is2xxSuccessful());
    }

    @Test
    void shouldReturn200AndSameFilmWhenPOSTFilms() throws Exception {
        Film film1 = Film.builder()
                .name("kobayashi")
                .description("abs")
                .duration(Duration.ofHours(1))
                .releaseDate(LocalDate.of(2021,1,21))
                .build();
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.description").value("abs"))
                .andExpect(jsonPath("$.duration").value("PT1H"))
                .andExpect(jsonPath("$.releaseDate").value("2021-01-21"));
    }
    @Test
    void shouldReturn500WhenPOSTFilmsAndNameIsBlank() throws Exception {
        Film film1 = Film.builder()
                .name(" ")
                .description("szx")
                .duration(Duration.ofHours(1))
                .releaseDate(LocalDate.of(2021,1,21))
                .build();
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn500WhenDescriptionIsLongerThen200ForPOSTFilms() throws Exception{
        Film film1 = Film.builder()
                .name("random")
                .description("szxromwvnzqdbaorjpxcxkppssvnzjawgfjqycaoayuuygvispeygxxmjbvqifvas" +
                        "ecdxizdmhwzsnayidimpulhlqamnsdnvngidohmgsquqixonxlkauvsnraahnmtzg" +
                        "oeslftgjmabiqziufqcohfzetvnugvoibnvjuxtzylxholziqfbabviomgvhdlmeve" +
                        "qxqernxaqwcyujqxfccqyesaydpkdvxfuvrdoeniivxqamgykwwsgcteauoiylbqladcwajvrsdqs" +
                        "qttcxvyoohbtxowlhsmflnzshlzjgaweizafahddqxmvyvzcafkfrjipdfjdgwfolnydykkpkbwszanmnf")
                .duration(Duration.ofHours(1))
                .releaseDate(LocalDate.of(2021,1,21))
                .build();
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result->result.getResolvedException().getClass().equals(ValidationException.class));

    }
    @Test
    void shouldReturn500WhenDateIsTooEarlyForPOSTFilms() throws Exception{
        Film film1 = Film.builder()
                .name("random")
                .description("szx")
                .duration(Duration.ofHours(1))
                .releaseDate(LocalDate.of(1895,12,27))
                .build();
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result->result.getResolvedException().getClass().equals(ValidationException.class));

    }
    @Test
    void shouldReturn500WhenDurationIsNegativeForPOSTFilms() throws Exception{
        Film film1 = Film.builder()
                .name("random")
                .description("szx")
                .duration(Duration.ofHours(-1))
                .releaseDate(LocalDate.of(1995,12,27))
                .build();
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result->result.getResolvedException().getClass().equals(ValidationException.class));

    }
    @Test
    void shouldReturn500WhenIdIsWrongForPUTFilms() throws Exception{
        Film film1 = Film.builder()
                .id(-1)
                .name("random")
                .description("szx")
                .duration(Duration.ofHours(1))
                .releaseDate(LocalDate.of(1995,12,27))
                .build();
        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(film1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result->result.getResolvedException().getClass().equals(ValidationException.class));
    }
    @Test
    void shouldReturn200AndSameFilmWhenPUTFilms() throws Exception {
        Film film1 = Film.builder()
                .name("kobayashi")
                .description("abs")
                .duration(Duration.ofHours(1))
                .releaseDate(LocalDate.of(2021,1,21))
                .build();
        Film film2 = Film.builder()
                .id(0)
                .name("kobayashi")
                .description("abse")
                .duration(Duration.ofHours(1))
                .releaseDate(LocalDate.of(2021,1,21))
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
                .andExpect(jsonPath("$.duration").value("PT1H"))
                .andExpect(jsonPath("$.releaseDate").value("2021-01-21"));
    }
    @Test
    void shouldReturn500WhenPUTFilmsAndNameIsBlank() throws Exception {
        Film film1 = Film.builder()
                .name("kobayashi")
                .description("szx")
                .duration(Duration.ofHours(1))
                .releaseDate(LocalDate.of(2021,1,21))
                .build();
        Film film2 = Film.builder()
                .id(0)
                .name("  ")
                .description("abs")
                .duration(Duration.ofHours(1))
                .releaseDate(LocalDate.of(2021,1,21))
                .build();
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film1))
                                .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                put("/films")
                        .content(objectMapper.writeValueAsString(film2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result->result.getResolvedException().getClass().equals(ValidationException.class));

    }

    @Test
    void shouldReturn500WhenDescriptionIsLongerThen200ForPUTFilms() throws Exception{
        Film film1 = Film.builder()
                .name("random")
                .description("szx")
                .duration(Duration.ofHours(1))
                .releaseDate(LocalDate.of(2021,1,21))
                .build();
        Film film2 = Film.builder()
                .id(0)
                .name("random")
                .description("szxromwvnzqdbaorjpxcxkppssvnzjawgfjqycaoayuuygvispeygxxmjbvqifvas" +
                        "ecdxizdmhwzsnayidimpulhlqamnsdnvngidohmgsquqixonxlkauvsnraahnmtzg" +
                        "oeslftgjmabiqziufqcohfzetvnugvoibnvjuxtzylxholziqfbabviomgvhdlmeve" +
                        "qxqernxaqwcyujqxfccqyesaydpkdvxfuvrdoeniivxqamgykwwsgcteauoiylbqladcwajvrsdqs" +
                        "qttcxvyoohbtxowlhsmflnzshlzjgaweizafahddqxmvyvzcafkfrjipdfjdgwfolnydykkpkbwszanmnf")
                .duration(Duration.ofHours(1))
                .releaseDate(LocalDate.of(2021,1,21))
                .build();

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film1))
                                .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                put("/films")
                        .content(objectMapper.writeValueAsString(film2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result->result.getResolvedException().getClass().equals(ValidationException.class));

    }
    @Test
    void shouldReturn500WhenDateIsTooEarlyForPUTFilms() throws Exception{
        Film film1 = Film.builder()
                .name("random")
                .description("szx")
                .duration(Duration.ofHours(1))
                .releaseDate(LocalDate.of(1995,12,27))
                .build();
        Film film2 = Film.builder()
                .id(0)
                .name("random")
                .description("szx")
                .duration(Duration.ofHours(1))
                .releaseDate(LocalDate.of(1895,12,27))
                .build();
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film1))
                                .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(film2))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result->result.getResolvedException().getClass().equals(ValidationException.class));


    }
    @Test
    void shouldReturn500WhenDurationIsNegativeForPUTFilms() throws Exception{
        Film film1 = Film.builder()
                .name("random")
                .description("szx")
                .duration(Duration.ofHours(1))
                .releaseDate(LocalDate.of(1995,12,27))
                .build();
        Film film2 = Film.builder()
                .id(0)
                .name("random")
                .description("szx")
                .duration(Duration.ofHours(-1))
                .releaseDate(LocalDate.of(1995,12,27))
                .build();
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film1))
                                .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(film2))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result->result.getResolvedException().getClass().equals(ValidationException.class));

    }

}

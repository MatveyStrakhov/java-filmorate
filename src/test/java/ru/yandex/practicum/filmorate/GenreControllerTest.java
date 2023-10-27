package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureTestDatabase
public class GenreControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn200ForGETGenres() throws Exception {
        mockMvc.perform(get("/genres")).andExpect(status().is2xxSuccessful());
    }

    @Test
    void shouldReturn200AndCorrectGenreForGetGenreById() throws Exception {
        mockMvc.perform(get("/genres/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Комедия"));
    }

    @Test
    void shouldReturn4xxFofGETGenreByIdWhenIncorrectId() throws Exception {
        mockMvc.perform(get("/genres/9999"))
                .andExpect(status().isNotFound());
    }

}

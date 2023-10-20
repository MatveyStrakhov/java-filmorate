package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RatingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn200ForGETRating() throws Exception {
        mockMvc.perform(get("/mpa")).andExpect(status().is2xxSuccessful());
    }

    @Test
    void shouldReturn200AndCorrectRatingForGetRatingById() throws Exception {
        mockMvc.perform(get("/mpa/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("G"));
    }

    @Test
    void shouldReturn4xxFofGETRatingByIdWhenIncorrectId() throws Exception {
        mockMvc.perform(get("/mpa/9999"))
                .andExpect(status().isNotFound());
    }

}

package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Director;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureTestDatabase
public class DirectorControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    void shouldReturn200AndSameDirectorWhenPOSTDirectors() throws Exception {
        Director director = Director.builder()
                .directorName("New Director")
                .build();
        mockMvc.perform(
                        post("/directors")
                                .content(objectMapper.writeValueAsString(director))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Director"));
    }

    @Test
    @Order(2)
    void shouldReturn200ForGETDirectors() throws Exception {
        mockMvc.perform(get("/directors"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @Order(3)
    void shouldReturn200AndNewDirectorForPutDirectors() throws Exception {
        Director director = Director.builder()
                .directorId(1)
                .directorName("Updated Director")
                .build();
        mockMvc.perform(put("/directors")
                        .content(objectMapper.writeValueAsString(director))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Director"));
    }

    @Test
    @Order(4)
    void shouldReturn200AndNewDirectorForGETDirectorById() throws Exception {
        mockMvc.perform(get("/directors/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Director"));
    }

    @Test
    @Order(5)
    void shouldReturn4xxForGETDirectorByIdWhenIncorrectId() throws Exception {
        mockMvc.perform(get("/directors/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(6)
    void shouldReturnBadRequestWhenPOSTDirectorsForDirectorWithoutName() throws Exception {
        Director director = Director.builder()
                .build();
        mockMvc.perform(
                        post("/directors")
                                .content(objectMapper.writeValueAsString(director))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    void shouldCorrectlyDeleteDirector() throws Exception {
        mockMvc.perform(delete("/directors/1"))
                .andExpect(status().is2xxSuccessful());
        mockMvc.perform(get("/directors/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(8)
    void shouldReturnNotFoundWhenDeleteNonExistentDirector() throws Exception {
        mockMvc.perform(delete("/directors/1"))
                .andExpect(status().isNotFound());
    }


}

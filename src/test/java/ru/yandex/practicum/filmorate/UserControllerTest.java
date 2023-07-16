package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn200ForGETUsers() throws Exception {
        mockMvc.perform(get("/users")).andExpect(status().is2xxSuccessful());
    }

    @Test
    void shouldReturn200AndSameUserWhenPOSTUsers() throws Exception {
        User user1 = User.builder()
                .login("login")
                .email("somemail@email.com")
                .birthday(LocalDate.of(2021, 12, 19))
                .name("toddler")
                .build();
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.name").value("toddler"))
                .andExpect(jsonPath("$.email").value("somemail@email.com"))
                .andExpect(jsonPath("$.birthday").value("2021-12-19"));
    }

    @Test
    void shouldReturn200AndNameIsLoginWhenPOSTUsersAndNameIsBlank() throws Exception {
        User user1 = User.builder()
                .login("login")
                .email("somemail@email.com")
                .birthday(LocalDate.of(2021, 12, 19))
                .name("  ")
                .build();

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.name").value("login"));
    }

    @Test
    void shouldReturn200AndNameIsLoginWhenPOSTUsersAndNameIsEmpty() throws Exception {
        User user1 = User.builder()
                .login("login")
                .email("somemail@email.com")
                .birthday(LocalDate.of(2021, 12, 19))
                .name("")
                .build();
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.name").value("login"));
    }

    @Test
    void shouldReturn500LoginIsEmptyForPOSTUsers() throws Exception {
        User user1 = User.builder()
                .login("")
                .email("somemail@email.com")
                .birthday(LocalDate.of(2021, 12, 19))
                .name("abc")
                .build();
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn500LoginContainsBlanksForPOSTUsers() throws Exception {
        User user1 = User.builder()
                .login("this is blank")
                .email("somemail@email.com")
                .birthday(LocalDate.of(2021, 12, 19))
                .name("abc")
                .build();
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn500EmailIsIncorrectForPOSTUsers() throws Exception {
        User user1 = User.builder()
                .login("login")
                .email("@somemailemail.com")
                .birthday(LocalDate.of(2021, 12, 19))
                .name("abc")
                .build();
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn500BirthdayIsInFutureForPOSTUsers() throws Exception {
        User user1 = User.builder()
                .login("login")
                .email("somemail@email.com")
                .birthday(LocalDate.of(3021, 12, 19))
                .name("abc")
                .build();
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user1))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn200AndSameUserWhenPUTUsers() throws Exception {
        User user1 = User.builder()
                .login("login")
                .email("somemail@email.com")
                .birthday(LocalDate.of(2021, 12, 19))
                .name("toddler")
                .build();
        User user2 = User.builder()
                .id(0)
                .login("login")
                .email("somemail@email.com")
                .birthday(LocalDate.of(2021, 12, 19))
                .name("toddler2")
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(user2))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.name").value("toddler2"))
                .andExpect(jsonPath("$.email").value("somemail@email.com"))
                .andExpect(jsonPath("$.birthday").value("2021-12-19"));
    }

    @Test
    void shouldReturn200AndNameIsLoginWhenPUTUsersAndNameIsBlank() throws Exception {
        User user1 = User.builder()
                .login("login")
                .email("somemail@email.com")
                .birthday(LocalDate.of(2021, 12, 19))
                .name("name")
                .build();
        User user2 = User.builder()
                .id(0)
                .login("login")
                .email("somemail@email.com")
                .birthday(LocalDate.of(2021, 12, 19))
                .name("  ")
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(user2))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.name").value("login"));
    }

    @Test
    void shouldReturn200AndNameIsLoginWhenPUTUsersAndNameIsEmpty() throws Exception {
        User user1 = User.builder()
                .login("login")
                .email("somemail@email.com")
                .birthday(LocalDate.of(2021, 12, 19))
                .name("name")
                .build();
        User user2 = User.builder()
                .id(0)
                .login("login")
                .email("somemail@email.com")
                .birthday(LocalDate.of(2021, 12, 19))
                .name("")
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(user2))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.name").value("login"));
    }

    @Test
    void shouldReturn500LoginIsEmptyForPUTUsers() throws Exception {
        User user1 = User.builder()
                .login("abc")
                .email("somemail@email.com")
                .birthday(LocalDate.of(2021, 12, 19))
                .name("abc")
                .build();
        User user2 = User.builder()
                .id(0)
                .login("")
                .email("somemail@email.com")
                .birthday(LocalDate.of(2021, 12, 19))
                .name("abc")
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(user2))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn500LoginContainsBlanksForPUTUsers() throws Exception {
        User user1 = User.builder()
                .login("thisisnotblank")
                .email("somemail@email.com")
                .birthday(LocalDate.of(2021, 12, 19))
                .name("abc")
                .build();
        User user2 = User.builder()
                .id(0)
                .login("this is blank")
                .email("somemail@email.com")
                .birthday(LocalDate.of(2021, 12, 19))
                .name("abc")
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user2))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn500EmailIsIncorrectForPUTUsers() throws Exception {
        User user1 = User.builder()
                .login("login")
                .email("somemail@email.com")
                .birthday(LocalDate.of(2021, 12, 19))
                .name("abc")
                .build();
        User user2 = User.builder()
                .id(0)
                .login("login")
                .email("@somemailemail.com")
                .birthday(LocalDate.of(2021, 12, 19))
                .name("abc")
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(user2))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn500BirthdayIsInFutureForPUTUsers() throws Exception {
        User user1 = User.builder()
                .login("login")
                .email("somemail@email.com")
                .birthday(LocalDate.of(2021, 12, 19))
                .name("abc")
                .build();
        User user2 = User.builder()
                .id(0)
                .login("login")
                .email("somemail@email.com")
                .birthday(LocalDate.of(3021, 12, 19))
                .name("abc")
                .build();
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                        put("/users")
                                .content(objectMapper.writeValueAsString(user2))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

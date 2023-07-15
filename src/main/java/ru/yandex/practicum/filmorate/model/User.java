package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
public class User {
    int id;
    @Email
    String email;
    @NotNull(message = "login mustn't be null")
    @NotBlank(message = "login mustn't be blank")
    String login;
    String name;
    LocalDate birthday;
}

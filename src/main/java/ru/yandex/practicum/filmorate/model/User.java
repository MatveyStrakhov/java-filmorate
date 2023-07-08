package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class User {
    int id;
    @Email
    String email;
    @NotNull
    @NotBlank
    String login;
    String name;
    LocalDate birthday;
}

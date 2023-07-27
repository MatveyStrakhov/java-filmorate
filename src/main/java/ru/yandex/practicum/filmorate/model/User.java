package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;
import ru.yandex.practicum.filmorate.annotation.BirthdayConstraint;
import ru.yandex.practicum.filmorate.annotation.LoginConstraint;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private int id;
    @Email
    private String email;
    @NotNull(message = "login mustn't be null")
    @NotBlank(message = "login mustn't be blank")
    @LoginConstraint
    private String login;
    @Nullable
    private String name;
    @BirthdayConstraint
    private LocalDate birthday;
    @JsonIgnore
    private final Set<Integer> friends = new HashSet<>();
}

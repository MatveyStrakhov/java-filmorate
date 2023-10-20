package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.Nullable;
import ru.yandex.practicum.filmorate.annotation.BirthdayConstraint;
import ru.yandex.practicum.filmorate.annotation.LoginConstraint;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@EqualsAndHashCode()
public class User {
    @Id
    @GeneratedValue
    private int id;
    @Email
    private String email;
    @NotNull(message = "login mustn't be null")
    @NotBlank(message = "login mustn't be blank")
    @LoginConstraint
    private String login;
    private String name;
    @NotNull
    @BirthdayConstraint
    private LocalDate birthday;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("id", id);
        values.put("email", email);
        values.put("name", name);
        values.put("login", login);
        values.put("birthday", birthday);
        return values;
    }
}

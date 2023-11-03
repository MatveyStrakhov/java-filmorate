package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserStorageTest {
    private final UserStorage userStorage;
    private final User user = User.builder()
            .login("login")
            .email("somemail@email.com")
            .birthday(LocalDate.of(2021, 12, 19))
            .name("toddler")
            .build();
    private final User user2 = User.builder()
            .login("login2")
            .email("somemail@email.com")
            .birthday(LocalDate.of(2021, 12, 19))
            .name("name")
            .build();
    private final User updatedUser = User.builder()
            .id(1)
            .login("login")
            .email("somemail@email.com")
            .birthday(LocalDate.of(2021, 12, 19))
            .name("BigGuy")
            .build();

    @Test
    @Order(0)
    void createUserTest() {
        assertThat(userStorage.createUser(user))
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("email", "somemail@email.com")
                .hasFieldOrPropertyWithValue("login", "login")
                .hasFieldOrPropertyWithValue("name", "toddler");
    }

    @Test
    @Order(1)
    void returnAllUsersTest() {
        assertThat(userStorage.returnAllUsers())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    @Order(2)
    void updateUserTest() {
        assertThat(userStorage.updateUser(updatedUser))
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("email", "somemail@email.com")
                .hasFieldOrPropertyWithValue("login", "login")
                .hasFieldOrPropertyWithValue("name", "BigGuy");
    }

    @Test
    @Order(3)
    void getUserByIdTest() {
        assertThat(userStorage.getUserById(1))
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("email", "somemail@email.com")
                .hasFieldOrPropertyWithValue("login", "login")
                .hasFieldOrPropertyWithValue("name", "BigGuy");
    }

    @Test
    @Order(4)
    void addFriendTest() {
        userStorage.createUser(user2);
        assertThat(userStorage.addFriend(1, 2)).isTrue();
    }

    @Test
    @Order(5)
    void getFriendsListTest() {
        assertThat(userStorage.getFriendsList(1)).hasSize(1);
    }

    @Test
    @Order(6)
    void removeFriendTest() {
        assertThat(userStorage.removeFriend(1, 2)).isTrue();
        assertThat(userStorage.getFriendsList(1)).hasSize(0);
    }

    @Test
    @Order(7)
    void deleteUserTest() {
        assertThat(userStorage.deleteUser(2)).isTrue();
        assertThat(userStorage.returnAllUsers()).hasSize(1);
    }

    @Test
    @Order(8)
    void isValidUserTest() {
        assertThat(userStorage.isValidUser(1)).isTrue();
        assertThat(userStorage.isValidUser(2)).isFalse();
    }
}

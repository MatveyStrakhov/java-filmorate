package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;

@Component
public class EventDao implements EventStorage {

    private final JdbcTemplate jdbcTemplate;

    public EventDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void eventAdd(int entityId, String eventType, String operation, int userId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlEvent = "INSERT INTO feeds (entityid, eventtype, operation, timestamp, user_id) VALUES(?,?,?,?,?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlEvent, Statement.RETURN_GENERATED_KEYS);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            ps.setInt(1, entityId);
            ps.setString(2, eventType);
            ps.setString(3, operation);
            ps.setLong(4, timestamp.getTime());
            ps.setInt(5, userId);
            return ps;
        }, keyHolder);
    }
}
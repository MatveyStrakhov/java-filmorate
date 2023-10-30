package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@Slf4j
public class FeedMapper implements RowMapper<Feed> {

    @Override
    public Feed mapRow(ResultSet rs, int rowNum) throws SQLException {
        log.info("feed building");
        return Feed.builder()
                .eventId(rs.getInt("eventid"))
                .entityId(rs.getInt("entityid"))
                .eventType(rs.getString("eventtype"))
                .operation(rs.getString("operation"))
                .timestamp(rs.getLong("timestamp"))
                .userId(rs.getInt("user_id"))
                .build();
    }
}
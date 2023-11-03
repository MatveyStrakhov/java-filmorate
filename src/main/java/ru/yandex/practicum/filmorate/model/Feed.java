package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Feed {

    private int eventId;
    private int entityId;
    private String eventType;
    private String operation;
    private Long timestamp;
    private int userId;

    public Feed(int eventId, int entityId, String eventType, String operation, Long timestamp, int userId) {
        this.eventId = eventId;
        this.entityId = entityId;
        this.eventType = eventType;
        this.operation = operation;
        this.timestamp = timestamp;
        this.userId = userId;
    }
}

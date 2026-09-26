package com.example.csvteamstats.service;

import com.example.csvteamstats.avro.TeamStatsKey;
import com.example.csvteamstats.avro.TeamStatsValue;
import com.example.csvteamstats.error.NonRetryableCsvException;
import com.example.csvteamstats.mapper.TeamStatsMessageMapper;
import com.example.csvteamstats.parser.TeamStatsCsvParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@Service
public class TeamStatsPublishService {
    static final int CHUNK_SIZE = 500;

    private final TeamStatsCsvParser parser;
    private final TeamStatsMessageMapper mapper;
    private final KafkaTemplate<TeamStatsKey, TeamStatsValue> kafka;
    private final String topic;

    public TeamStatsPublishService(
            TeamStatsCsvParser parser,
            TeamStatsMessageMapper mapper,
            KafkaTemplate<TeamStatsKey, TeamStatsValue> kafka,
            @Value("${app.kafka.topics.parsed-team-stats}") String topic) {
        this.parser = parser;
        this.mapper = mapper;
        this.kafka = kafka;
        this.topic = topic;
    }

    public void publishFile(String eventId, String filePath) {
        try {
            parser.parseInChunks(Path.of(filePath), CHUNK_SIZE, rows -> {
                CompletableFuture<?>[] sends = rows.stream()
                        .map(row -> kafka.send(topic, mapper.toKey(eventId, row), mapper.toValue(eventId, row)))
                        .toArray(CompletableFuture[]::new);
                CompletableFuture.allOf(sends).join();
            });
        } catch (Exception exception) {
            if (containsKafkaFailure(exception)) {
                if (exception instanceof RuntimeException runtimeException) {
                    throw runtimeException;
                }
                throw new RuntimeException(exception);
            }
            throw new NonRetryableCsvException("Unable to parse TEAM_STATS CSV: " + filePath, exception);
        }
    }

    private boolean containsKafkaFailure(Throwable error) {
        Throwable current = error;
        while (current != null) {
            if (current instanceof org.springframework.kafka.KafkaException
                    || current instanceof org.apache.kafka.common.KafkaException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}

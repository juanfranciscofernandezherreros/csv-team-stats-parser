package com.example.csvteamstats.service;

import com.example.csvteamstats.avro.TeamStatsKey;
import com.example.csvteamstats.avro.TeamStatsValue;
import com.example.csvteamstats.dto.TeamStatsDTO;
import com.example.csvteamstats.mapper.TeamStatsMessageMapper;
import com.example.csvteamstats.parser.TeamStatsCsvParser;
import org.apache.kafka.common.KafkaException;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TeamStatsPublishServiceTest {

    @Test
    void sendsWholeChunkBeforeWaitingForFirstAck() throws Exception {
        TeamStatsCsvParser parser = mock(TeamStatsCsvParser.class);
        TeamStatsMessageMapper mapper = mock(TeamStatsMessageMapper.class);
        @SuppressWarnings("unchecked")
        KafkaTemplate<TeamStatsKey, TeamStatsValue> kafka = mock(KafkaTemplate.class);
        TeamStatsDTO first = mock(TeamStatsDTO.class);
        TeamStatsDTO second = mock(TeamStatsDTO.class);
        TeamStatsKey key = mock(TeamStatsKey.class);
        TeamStatsValue value = mock(TeamStatsValue.class);

        doAnswer(call -> {
            @SuppressWarnings("unchecked")
            java.util.function.Consumer<List<TeamStatsDTO>> consumer = call.getArgument(2);
            consumer.accept(List.of(first, second));
            return null;
        }).when(parser).parseInChunks(eq(Path.of("/data/team.csv")), eq(500), any());

        when(mapper.toKey(any(), any())).thenReturn(key);
        when(mapper.toValue(any(), any())).thenReturn(value);

        CompletableFuture<Object> firstAck = new CompletableFuture<>();
        CountDownLatch secondSent = new CountDownLatch(1);
        java.util.concurrent.atomic.AtomicInteger sends = new java.util.concurrent.atomic.AtomicInteger();
        when(kafka.send("team-stats.parsed", key, value)).thenAnswer(invocation -> {
            if (sends.incrementAndGet() == 1) return firstAck;
            secondSent.countDown();
            return CompletableFuture.completedFuture(null);
        });

        TeamStatsPublishService service = new TeamStatsPublishService(parser, mapper, kafka, "team-stats.parsed");
        var executor = Executors.newSingleThreadExecutor();
        try {
            var publish = executor.submit(() -> service.publishFile("e1", "/data/team.csv"));
            assertTrue(secondSent.await(1, TimeUnit.SECONDS));
            firstAck.complete(null);
            publish.get(2, TimeUnit.SECONDS);
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void failedAckFailsProcessing() throws Exception {
        TeamStatsCsvParser parser = mock(TeamStatsCsvParser.class);
        TeamStatsMessageMapper mapper = mock(TeamStatsMessageMapper.class);
        @SuppressWarnings("unchecked")
        KafkaTemplate<TeamStatsKey, TeamStatsValue> kafka = mock(KafkaTemplate.class);
        TeamStatsDTO row = mock(TeamStatsDTO.class);
        TeamStatsKey key = mock(TeamStatsKey.class);
        TeamStatsValue value = mock(TeamStatsValue.class);

        doAnswer(call -> {
            @SuppressWarnings("unchecked")
            java.util.function.Consumer<List<TeamStatsDTO>> consumer = call.getArgument(2);
            consumer.accept(List.of(row));
            return null;
        }).when(parser).parseInChunks(any(Path.class), eq(500), any());

        when(mapper.toKey(any(), any())).thenReturn(key);
        when(mapper.toValue(any(), any())).thenReturn(value);
        when(kafka.send(any(), any(), any()))
                .thenReturn((CompletableFuture) CompletableFuture.failedFuture(new KafkaException("down")));

        TeamStatsPublishService service = new TeamStatsPublishService(parser, mapper, kafka, "team-stats.parsed");
        assertThrows(RuntimeException.class, () -> service.publishFile("e1", "/data/team.csv"));
    }
}

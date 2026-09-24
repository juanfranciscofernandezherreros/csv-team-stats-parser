package com.example.csvteamstats.service;
import com.example.csvteamstats.avro.*;
import com.example.csvteamstats.mapper.TeamStatsMessageMapper;
import com.example.csvteamstats.parser.TeamStatsCsvParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.nio.file.Path;
@Service
public class TeamStatsPublishService {
 static final int CHUNK_SIZE=500;
 private final TeamStatsCsvParser parser; private final TeamStatsMessageMapper mapper; private final KafkaTemplate<TeamStatsKey,TeamStatsValue> kafka; private final String topic;
 public TeamStatsPublishService(TeamStatsCsvParser parser,TeamStatsMessageMapper mapper,KafkaTemplate<TeamStatsKey,TeamStatsValue> kafka,@Value("${app.kafka.topics.parsed-team-stats}") String topic){this.parser=parser;this.mapper=mapper;this.kafka=kafka;this.topic=topic;}
 public void publishFile(String eventId,String filePath){
  try{parser.parseInChunks(Path.of(filePath),CHUNK_SIZE,rows->rows.forEach(d->kafka.send(topic,mapper.toKey(eventId,d),mapper.toValue(eventId,d)).join()));}
  catch(Exception e){throw new IllegalStateException("Error al publicar TEAM_STATS parseado: "+filePath,e);}
 }
}

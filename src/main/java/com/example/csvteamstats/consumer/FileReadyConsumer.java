package com.example.csvteamstats.consumer;
import com.example.csvteamstats.service.TeamStatsPublishService;
import com.example.csvteamstats.validation.FileEventValidator;
import com.example.csvwatcher.watcher.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
@Service
public class FileReadyConsumer {
 private final TeamStatsPublishService publisher; private final FileEventValidator validator;
 public FileReadyConsumer(TeamStatsPublishService publisher,FileEventValidator validator){this.publisher=publisher;this.validator=validator;}
 @KafkaListener(topics="${app.kafka.topics.file-ready}",groupId="${spring.kafka.consumer.group-id}")
 public void listen(ConsumerRecord<FileEventKey,FileEventValue> record){
  FileEventValue value=record.value();
  if(value!=null&&value.getFileType()!=null&&!value.getFileType().isBlank()&&!"TEAM_STATS".equalsIgnoreCase(value.getFileType())) return;
  var path=validator.validate(record.key(),value);
  publisher.publishFile(record.key().getUniqueId(),path.toString());
 }
}

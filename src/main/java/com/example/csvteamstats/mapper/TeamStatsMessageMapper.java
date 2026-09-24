package com.example.csvteamstats.mapper;
import com.example.csvteamstats.avro.*;
import com.example.csvteamstats.dto.TeamStatsDTO;
import org.springframework.stereotype.Component;
@Component
public class TeamStatsMessageMapper {
 public TeamStatsKey toKey(String eventId,TeamStatsDTO d){return TeamStatsKey.newBuilder().setSourceEventId(eventId).setMatchId(d.getMatchId()).setPeriod(d.getPeriod()).setCategory(d.getCategory()).setMetric(d.getMetric()).build();}
 public TeamStatsValue toValue(String eventId,TeamStatsDTO d){return TeamStatsValue.newBuilder().setSourceEventId(eventId).setMatchId(d.getMatchId()).setPeriod(d.getPeriod()).setCategory(d.getCategory()).setMetric(d.getMetric()).setHomeTeam(d.getHomeTeam()).setHomeValue(d.getHomeValue()).setAwayTeam(d.getAwayTeam()).setAwayValue(d.getAwayValue()).setSourceUrl(d.getSourceUrl()).build();}
}

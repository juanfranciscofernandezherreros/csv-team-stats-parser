package com.example.csvteamstats.mapper;
import com.example.csvteamstats.dto.TeamStatsDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class TeamStatsMessageMapperTest {
 @Test void mapsKeyAndValue(){
  TeamStatsDTO d=new TeamStatsDTO();
  d.setMatchId("m1");d.setPeriod("Overall");d.setCategory("Scoring");d.setMetric("FGA");
  d.setHomeTeam("A");d.setHomeValue("60");d.setAwayTeam("B");d.setAwayValue("55");
  var mapper=new TeamStatsMessageMapper();
  var key=mapper.toKey("e1",d);var value=mapper.toValue("e1",d);
  assertEquals("m1",key.getMatchId());
  assertEquals("FGA",key.getMetric());
  assertEquals("60",value.getHomeValue());
 }
}

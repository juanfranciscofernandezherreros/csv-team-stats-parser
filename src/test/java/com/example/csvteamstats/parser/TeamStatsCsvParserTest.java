package com.example.csvteamstats.parser;
import com.example.csvteamstats.dto.TeamStatsDTO;
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class TeamStatsCsvParserTest {
 @Test void parsesOverallAndQuarterRows() throws Exception {
  Path f=Files.createTempFile("stats_all_periods",".csv");
  Files.writeString(f,"match_id,period,category,metric,home_team,home_value,away_team,away_value,source_url\n0fAJZWz1,Overall,Scoring,Field Goal %,Bamberg,45%,Bayern,55%,https://example\n0fAJZWz1,1st Quarter,Scoring,Field Goal Attempts,Bamberg,16,Bayern,15,https://example\n");
  List<TeamStatsDTO> rows=new ArrayList<>();
  new TeamStatsCsvParser().parseInChunks(f,500,rows::addAll);
  assertEquals(2,rows.size());
  assertEquals("Field Goal Attempts",rows.get(1).getMetric());
  assertEquals("16",rows.get(1).getHomeValue());
 }
 @Test void rejectsWrongColumnCount() throws Exception {
  Path f=Files.createTempFile("stats_all_periods",".csv");
  Files.writeString(f,"a,b\n1,2\n");
  assertThrows(IllegalArgumentException.class,()->new TeamStatsCsvParser().parseInChunks(f,500,x->{}));
 }
}

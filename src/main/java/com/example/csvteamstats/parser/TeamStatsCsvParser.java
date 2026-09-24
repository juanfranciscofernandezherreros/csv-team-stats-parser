package com.example.csvteamstats.parser;
import com.example.csvteamstats.dto.TeamStatsDTO;
import org.apache.commons.csv.*;
import org.springframework.stereotype.Component;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;
@Component
public class TeamStatsCsvParser {
 private static final int EXPECTED_COLUMNS=9;
 public void parseInChunks(Path path,int chunkSize,Consumer<List<TeamStatsDTO>> consumer) throws IOException {
  if(chunkSize<=0) throw new IllegalArgumentException("chunkSize debe ser mayor que 0");
  try(Reader reader=Files.newBufferedReader(path);CSVParser parser=CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setTrim(true).build().parse(reader)){
   List<TeamStatsDTO> chunk=new ArrayList<>(Math.min(chunkSize,1000));
   for(CSVRecord r:parser){
    if(r.size()!=EXPECTED_COLUMNS) throw new IllegalArgumentException("Fila CSV "+r.getRecordNumber()+" con "+r.size()+" columnas; se esperaban "+EXPECTED_COLUMNS);
    TeamStatsDTO d=toDto(r);
    if(d.getMatchId().isBlank()||d.getPeriod().isBlank()||d.getMetric().isBlank()) throw new IllegalArgumentException("Fila CSV "+r.getRecordNumber()+" sin match_id, period o metric");
    chunk.add(d);
    if(chunk.size()==chunkSize){consumer.accept(List.copyOf(chunk));chunk.clear();}
   }
   if(!chunk.isEmpty()) consumer.accept(List.copyOf(chunk));
  }
 }
 TeamStatsDTO toDto(CSVRecord r){
  TeamStatsDTO d=new TeamStatsDTO();
  d.setMatchId(r.get(0));d.setPeriod(r.get(1));d.setCategory(r.get(2));d.setMetric(r.get(3));
  d.setHomeTeam(r.get(4));d.setHomeValue(blankToNull(r.get(5)));d.setAwayTeam(r.get(6));d.setAwayValue(blankToNull(r.get(7)));d.setSourceUrl(blankToNull(r.get(8)));
  return d;
 }
 private String blankToNull(String v){return v==null||v.isBlank()?null:v;}
}

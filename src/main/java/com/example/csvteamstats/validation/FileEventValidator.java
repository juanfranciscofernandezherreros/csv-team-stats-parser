package com.example.csvteamstats.validation;
import com.example.csvteamstats.constants.CsvConstants;
import com.example.csvwatcher.watcher.*;
import org.apache.commons.csv.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.*;
import java.nio.file.*;
@Component
public class FileEventValidator {
 private final Path allowedRoot;
 public FileEventValidator(@Value("${app.csv.allowed-root:/data}") String allowedRoot){this.allowedRoot=Path.of(allowedRoot).toAbsolutePath().normalize();}
 public Path validate(FileEventKey key,FileEventValue value){
  if(key==null||key.getUniqueId()==null||key.getUniqueId().isBlank()) throw new IllegalArgumentException("Evento Kafka sin key valida");
  if(value==null||value.getFilePath()==null||value.getFilePath().isBlank()) throw new IllegalArgumentException("Evento Kafka sin filePath");
  Path path=Path.of(value.getFilePath());
  if(!path.isAbsolute()||!path.normalize().equals(path)) throw new IllegalArgumentException("filePath no valido: "+value.getFilePath());
  Path normalized=path.toAbsolutePath().normalize();
  if(!normalized.startsWith(allowedRoot)) throw new IllegalArgumentException("filePath fuera del directorio permitido: "+value.getFilePath());
  if(normalized.getFileName()==null||!CsvConstants.EXPECTED_FILENAME.equalsIgnoreCase(normalized.getFileName().toString())) throw new IllegalArgumentException("TEAM_STATS requiere stats_all_periods.csv");
  if(!Files.isRegularFile(normalized)||!Files.isReadable(normalized)) throw new IllegalArgumentException("Archivo inexistente o no legible: "+normalized);
  try(Reader reader=Files.newBufferedReader(normalized);CSVParser parser=CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build().parse(reader)){
   if(!CsvConstants.EXPECTED_HEADER.equals(parser.getHeaderNames())) throw new IllegalArgumentException("Cabecera no valida para TEAM_STATS");
  }catch(Exception e){if(e instanceof IllegalArgumentException iae) throw iae;throw new IllegalArgumentException("No se puede validar el CSV: "+normalized,e);}
  return normalized;
 }
}

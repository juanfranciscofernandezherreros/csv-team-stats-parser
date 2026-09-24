package com.example.csvteamstats.constants;
import java.util.List;
public final class CsvConstants {
 public static final String EXPECTED_FILENAME="stats_all_periods.csv";
 public static final List<String> EXPECTED_HEADER=List.of("match_id","period","category","metric","home_team","home_value","away_team","away_value","source_url");
 private CsvConstants(){}
}

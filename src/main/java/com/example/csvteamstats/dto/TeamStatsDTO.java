package com.example.csvteamstats.dto;
import lombok.Data;
@Data
public class TeamStatsDTO {
 private String matchId, period, category, metric, homeTeam, homeValue, awayTeam, awayValue, sourceUrl;
}

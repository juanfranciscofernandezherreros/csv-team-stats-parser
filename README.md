Current version: **1.0.1**

# csv-team-stats-parser

Microservicio que transforma las estadísticas de equipo generadas por `python-pyppeter`.

```text
file.ready.team-stats -> validación stats_all_periods.csv -> CSV parser -> Avro -> team-stats.parsed
```

Entrada exacta:

```text
match_id,period,category,metric,home_team,home_value,away_team,away_value,source_url
```

Publica un `TeamStatsKey / TeamStatsValue` por métrica y periodo. Conserva los valores como texto porque Flashscore mezcla enteros, decimales y porcentajes. No usa PostgreSQL, JPA ni Flyway.

Variables principales: `KAFKA_BOOTSTRAP_SERVERS`, `KAFKA_SCHEMA_REGISTRY_URL`, `KAFKA_FILE_READY_TOPIC`, `KAFKA_PARSED_TEAM_STATS_TOPIC`, `CSV_ALLOWED_ROOT`.

Tests: `mvn -B test`.

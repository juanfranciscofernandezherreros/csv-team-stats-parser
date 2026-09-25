![version](https://img.shields.io/badge/version-1.0.5-blue)
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

## Contratos Avro compartidos

`FileEventKey`, `FileEventValue`, `TeamStatsKey` y `TeamStatsValue` se consumen desde:

```text
com.fernandez.basketball:basketball-event-contracts:1.0.2
```

Este repositorio ya no mantiene copias locales de esos schemas ni genera clases Avro durante su propia build. Fuera de GitHub Actions, Maven necesita credenciales con `read:packages` para resolver el artefacto desde GitHub Packages.

Variables principales: `KAFKA_BOOTSTRAP_SERVERS`, `KAFKA_SCHEMA_REGISTRY_URL`, `KAFKA_FILE_READY_TOPIC`, `KAFKA_PARSED_TEAM_STATS_TOPIC`, `CSV_ALLOWED_ROOT`.

Tests: `mvn -B test`.

# Changelog

## 1.0.2 - 2026-09-25

- [patch] Añade eliminación automática de la rama origen después de mergear una Pull Request en `main`.

## 1.0.1 - 2026-09-25

- [patch] Exige confirmar rama y nivel SemVer antes de cualquier cambio.

## 1.0.0 - 2026-09-24
- Añade el parser de `stats_all_periods.csv`.
- Consume `file.ready.team-stats`.
- Publica `TeamStatsKey / TeamStatsValue` en `team-stats.parsed`.
- Conserva estadísticas Overall y por cuarto sin forzar tipos numéricos.

# Changelog

## 1.1.1 - 2026-09-26

- [patch] KAN-112 captura fallos de deserialización Avro mediante `ErrorHandlingDeserializer`.
- [patch] Permite publicar en DLT objetos Avro y bytes crudos mediante `DelegatingByTypeSerializer`.
- [patch] Deja que Kafka seleccione la partición DLT y propaga fallos de publicación en DLT.
- [patch] Añade cobertura del flujo deserialización fallida → DLT conservando los bytes originales.

## 1.1.0 - 2026-09-26

- [minor] KAN-112 aplica la estrategia común de errores Kafka de KAN-18.
- [minor] Separa errores permanentes de validación/CSV de fallos transitorios de Kafka.
- [minor] Configura retries/backoff y DLT `file.ready.team-stats.DLT`.
- [minor] Añade tests de clasificación de error permanente y transitorio.


## 1.0.5 - 2026-09-25

- [patch] KAN-86 sustituye los schemas locales FileEvent/TeamStats por `basketball-event-contracts:1.0.2`.
- [patch] Elimina la generación Avro local y configura Maven/CI con lectura autenticada de GitHub Packages.
- [patch] Mantiene los namespaces, campos y semántica Kafka existentes.

## 1.0.4 - 2026-09-25

- [patch] Refuerza AGENTS.md: lectura obligatoria por tarea, flujo autónomo y prohibición absoluta de escrituras directas en main.

## 1.0.3

- [patch] Estandariza la automatización del repositorio con el flujo autónomo de csv-results-parser.

## 1.0.2 - 2026-09-25

- [patch] Añade eliminación automática de la rama origen después de mergear una Pull Request en `main`.

## 1.0.1 - 2026-09-25

- [patch] Exige confirmar rama y nivel SemVer antes de cualquier cambio.

## 1.0.0 - 2026-09-24
- Añade el parser de `stats_all_periods.csv`.
- Consume `file.ready.team-stats`.
- Publica `TeamStatsKey / TeamStatsValue` en `team-stats.parsed`.
- Conserva estadísticas Overall y por cuarto sin forzar tipos numéricos.

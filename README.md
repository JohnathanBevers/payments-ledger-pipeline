# payments-ledger-pipeline

A practical project that simulates how real revenue finance systems work. It consumes payment events from Kafka, processes them
in a Java Spring Boot service, and stores them in Postgres as a reliable ledger. The focus is on correctness and trust, so it handles duplicates safely, supports replays, and produces simple daily reconciliation totals.

## What you get
- Spring Boot 3 service (`/app`) that consumes `payment_events` from Kafka and writes immutable ledger entries to Postgres
- Idempotency enforced through a unique constraint on `event_id`
- Reconciliation endpoint `/reconciliation?start=YYYY-MM-DD&end=YYYY-MM-DD&format=json|csv` returning daily totals by event type
- Flyway migrations in `app/src/main/resources/db/migration`
- Docker Compose stack for Kafka + Zookeeper + Postgres + the application
- Helper script to publish sample events with `kafka-console-producer`

## Running locally
```bash
docker compose up --build
```
The app listens on `http://localhost:8080`. Kafka is on `localhost:9092`, Postgres on `localhost:5432` (user/password: `payments`).

### Startup order and health checks
- Kafka waits for Zookeeper and exposes a health check via `kafka-broker-api-versions --bootstrap-server kafka:29092`.
- Postgres now uses a health check that runs `pg_isready` and a simple `SELECT 1` so the schema is reachable before the app starts.
- The Spring Boot app uses `depends_on` with `condition: service_healthy` for Kafka and Postgres, so it will only start after both dependencies pass their health checks. This lets you rely on `docker compose up` to bring everything up in the right order without manual retries.

To publish sample events after the stack is up:
```bash
./scripts/produce_sample_events.sh
```
The script uses `kafka-console-producer` if you have Kafka CLIs locally. If not, it automatically falls back to `docker compose exec kafka` so you can run it as long as the compose stack is up. Internally, Kafka uses `kafka:29092` for inter-container traffic and `localhost:9092` for host access; you can override either via `KAFKA_BOOTSTRAP_FOR_CONTAINER` or `KAFKA_BOOTSTRAP`.

## Replaying and reconciling
Replays are safe because duplicate `eventId` values are skipped via the database constraint. Use the reconciliation endpoint to inspect daily totals:
```bash
curl "http://localhost:8080/reconciliation?start=2024-01-01&end=2024-01-03&format=csv"
```

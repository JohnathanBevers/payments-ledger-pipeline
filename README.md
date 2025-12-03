# payments-ledger-pipeline
A practical project that simulates how real revenue finance systems work. It consumes payment events from Kafka, processes them in a Java Spring Boot service, and stores them in Postgres as a reliable ledger. The focus is on correctness and trust, so it handles duplicates safely, supports replays, and produces simple daily reconciliation totals.

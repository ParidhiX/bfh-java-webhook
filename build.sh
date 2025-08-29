#!/usr/bin/env bash
set -euo pipefail
echo "=== Building Spring Boot JAR with Maven ==="
mvn clean package -DskipTests
mkdir -p dist
cp target/bfh-java-webhook-1.0.0.jar dist/bfh-java-webhook.jar
echo "=== Done. JAR is at dist/bfh-java-webhook.jar ==="

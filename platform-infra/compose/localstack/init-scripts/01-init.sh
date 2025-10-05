#!/usr/bin/env bash
set -euo pipefail

echo "[init] criando recursos basicos no Localstack..."

awslocal s3 mb s3://dev-bucket || true
awslocal sqs create-queue --queue-name dev-fila-core || true
awslocal sqs create-queue --queue-name dev-fila-callback || true

echo "[init] pronto."
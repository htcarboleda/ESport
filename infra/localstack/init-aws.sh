#!/bin/bash
# infra/localstack/init-aws.sh
# Este script se ejecuta automáticamente cuando LocalStack termina de arrancar

echo '====================================================='
echo ' Inicializando recursos AWS en LocalStack...'
echo '====================================================='

# ── SQS: Dead Letter Queue primero ───────────────────────────────
awslocal sqs create-queue --queue-name esport-dlq
echo '[SQS] ✅ DLQ creada: esport-dlq'

# ── SQS: Colas con DLQ asociada y Long Polling ───────────────────
DLQ_ARN='arn:aws:sqs:us-east-1:000000000000:esport-dlq'

awslocal sqs create-queue --queue-name ticket-update-queue \
  --attributes file:///etc/localstack/init/ready.d/queue-attrs.json
echo '[SQS] ✅ Cola creada: ticket-update-queue'

awslocal sqs create-queue --queue-name notification-queue \
  --attributes '{"ReceiveMessageWaitTimeSeconds": "10"}'
echo '[SQS] ✅ Cola creada: notification-queue'

# ── SNS: Topic principal ─────────────────────────────────────────
awslocal sns create-topic --name tournament-events
echo '[SNS] ✅ Topic creado: tournament-events'

SNS_ARN='arn:aws:sns:us-east-1:000000000000:tournament-events'

# ── SNS: Suscribir las colas (fan-out) ───────────────────────────
awslocal sns subscribe \
  --topic-arn $SNS_ARN --protocol sqs \
  --notification-endpoint arn:aws:sqs:us-east-1:000000000000:ticket-update-queue
echo '[SNS] ✅ Suscripción: tournament-events → ticket-update-queue'

awslocal sns subscribe \
  --topic-arn $SNS_ARN --protocol sqs \
  --notification-endpoint arn:aws:sqs:us-east-1:000000000000:notification-queue
echo '[SNS] ✅ Suscripción: tournament-events → notification-queue'

# ── DynamoDB: Tabla de auditoría ─────────────────────────────────
awslocal dynamodb create-table \
  --table-name esport-audit-trail \
  --attribute-definitions AttributeName=eventId,AttributeType=S \
  --key-schema AttributeName=eventId,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST
echo '[DynamoDB] ✅ Tabla creada: esport-audit-trail'

# ── S3: Bucket para QR codes ─────────────────────────────────────
awslocal s3 mb s3://esport-qr-codes-local
echo '[S3] ✅ Bucket creado: esport-qr-codes-local'

echo '====================================================='
echo ' ✅ LocalStack inicializado correctamente'
echo '====================================================='


# Dar permisos de ejecución al script (importante en WSL)
chmod +x infra/localstack/init-aws.sh


output "bucket_uploads"          { description = "Bucket S3 de uploads"; value = try(aws_s3_bucket.uploads.bucket, null) }
output "topic_notifications_arn" { description = "ARN do tópico SNS de notificações"; value = try(aws_sns_topic.notifications.arn, null) }
output "sqs_email_url"           { description = "URL da fila SQS de e-mail"; value = try(aws_sqs_queue.email.id, null) }
output "sqs_sms_url"             { description = "URL da fila SQS de SMS"; value = try(aws_sqs_queue.sms.id, null) }
output "sqs_whatsapp_url"        { description = "URL da fila SQS de WhatsApp"; value = try(aws_sqs_queue.whatsapp.id, null) }
output "apigw_http_api_endpoint" { description = "Endpoint base do API Gateway HTTP"; value = try(aws_apigatewayv2_api.http_api.api_endpoint, null) }
output "nlb_dns_name"            { description = "DNS do NLB (simulado)"; value = try(aws_lb.nlb.dns_name, null) }
output "alb_dns_name"            { description = "DNS do ALB (simulado)"; value = try(aws_lb.alb.dns_name, null) }

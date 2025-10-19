output "bucket_name" {
  value = aws_s3_bucket.wos-eats-catalog.bucket
}

output "queue_url" {
  value = aws_sqs_queue.core.id
}

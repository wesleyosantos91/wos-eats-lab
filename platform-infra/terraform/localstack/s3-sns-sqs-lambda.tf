resource "aws_s3_bucket" "uploads" { bucket = "wos-eats-dev-uploads" }

resource "aws_sns_topic" "notifications" { name = "wos-eats-dev-notifications" }

resource "aws_sqs_queue" "email"    { name = "wos-eats-dev-email-queue" }
resource "aws_sqs_queue" "sms"      { name = "wos-eats-dev-sms-queue" }
resource "aws_sqs_queue" "whatsapp" { name = "wos-eats-dev-whatsapp-queue" }

resource "aws_sns_topic_subscription" "email_sub" {
  topic_arn = aws_sns_topic.notifications.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.email.arn
}
resource "aws_sns_topic_subscription" "sms_sub" {
  topic_arn = aws_sns_topic.notifications.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.sms.arn
}
resource "aws_sns_topic_subscription" "wa_sub" {
  topic_arn = aws_sns_topic.notifications.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.whatsapp.arn
}

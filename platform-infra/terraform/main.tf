resource "aws_s3_bucket" "wos-eats-catalog" { bucket = "wos-eats-catalog" }
resource "aws_sqs_queue" "core"   { name   = "dev-core"   }
resource "aws_s3_bucket" "bronze" { bucket = "dev-bronze" }
resource "aws_sqs_queue" "core"   { name   = "dev-core"   }
terraform {
  required_version = ">= 1.6.0"
  required_providers { aws = { source = "hashicorp/aws", version = "~> 5.60" } }
}
provider "aws" {
  region = "sa-east-1"
  access_key = "test"
  secret_key = "test"
  s3_use_path_style = true
  endpoints {
    apigateway   = "http://localhost:4566"
    apigatewayv2 = "http://localhost:4566"
    s3           = "http://localhost:4566"
    sqs          = "http://localhost:4566"
    sns          = "http://localhost:4566"
    lambda       = "http://localhost:4566"
    iam          = "http://localhost:4566"
    logs         = "http://localhost:4566"
    cloudwatch   = "http://localhost:4566"
    elbv2        = "http://localhost:4566"
    sts          = "http://localhost:4566"
    ec2          = "http://localhost:4566"
  }
}
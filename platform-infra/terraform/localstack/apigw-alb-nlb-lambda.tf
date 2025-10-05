# Lambda inline (Node 18) empacotada com archive_file
data "archive_file" "lambda_zip" {
  type        = "zip"
  output_path = "${path.module}/lambda-hello.zip"
  source {
    filename = "index.js"
    content  = <<-EOF
      exports.handler = async function(event) {
        return {
          statusCode: 200,
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ message: "hello from lambda", input: event })
        };
      }
    EOF
  }
}

resource "aws_lambda_function" "hello" {
  function_name    = "wos-eats-hello"
  filename         = data.archive_file.lambda_zip.output_path
  source_code_hash = data.archive_file.lambda_zip.output_base64sha256
  role             = aws_iam_role.lambda_exec.arn
  handler          = "index.handler"
  runtime          = "nodejs18.x"
}

# API Gateway HTTP (v2) -> Lambda proxy
resource "aws_apigatewayv2_api" "http_api" {
  name          = "wos-eats-http-api"
  protocol_type = "HTTP"
}

resource "aws_apigatewayv2_integration" "lambda_integration" {
  api_id                 = aws_apigatewayv2_api.http_api.id
  integration_type       = "AWS_PROXY"
  integration_uri        = aws_lambda_function.hello.invoke_arn
  payload_format_version = "2.0"
}

resource "aws_apigatewayv2_route" "hello_route" {
  api_id    = aws_apigatewayv2_api.http_api.id
  route_key = "GET /hello"
  target    = "integrations/${aws_apigatewayv2_integration.lambda_integration.id}"
}

resource "aws_lambda_permission" "allow_apigw" {
  statement_id  = "AllowAPIGatewayInvoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.hello.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_apigatewayv2_api.http_api.execution_arn}/*/*"
}

resource "aws_apigatewayv2_stage" "default" {
  api_id      = aws_apigatewayv2_api.http_api.id
  name        = "$default"
  auto_deploy = true
}

# NLB e ALB simulados (LocalStack aceita recursos)
resource "aws_lb" "nlb" {
  name               = "wos-eats-nlb"
  internal           = false
  load_balancer_type = "network"
  subnets            = ["subnet-00000000", "subnet-00000001"]
}

resource "aws_lb" "alb" {
  name               = "wos-eats-alb"
  internal           = false
  load_balancer_type = "application"
  subnets            = ["subnet-00000002", "subnet-00000003"]
}

resource "aws_lb_target_group" "tg_nlb" {
  name     = "wos-eats-tg-nlb"
  port     = 80
  protocol = "TCP"
  vpc_id   = "vpc-00000000"
}

resource "aws_lb_target_group" "tg_alb" {
  name     = "wos-eats-tg-alb"
  port     = 80
  protocol = "HTTP"
  vpc_id   = "vpc-00000000"
}

resource "aws_lb_listener" "nlb_listener" {
  load_balancer_arn = aws_lb.nlb.arn
  port              = 80
  protocol          = "TCP"
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.tg_nlb.arn
  }
}

resource "aws_lb_listener" "alb_listener" {
  load_balancer_arn = aws_lb.alb.arn
  port              = 80
  protocol          = "HTTP"
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.tg_alb.arn
  }
}

output "apigw_http_api_endpoint" { value = aws_apigatewayv2_api.http_api.api_endpoint }
output "nlb_dns_name"            { value = aws_lb.nlb.dns_name }
output "alb_dns_name"            { value = aws_lb.alb.dns_name }

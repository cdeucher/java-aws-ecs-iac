# AWS ECS SDK IAC

## Build
- `mvn package`

## Environments
- AWS_PROFILE=""
- AWS_ACCESS_KEY_ID=""
- AWS_SECRET_ACCESS_KEY=""
- AWS_DEFAULT_REGION=us-east-1
// https://docs.aws.amazon.com/AmazonECS/latest/developerguide/task-iam-roles.html
- AWS_IAM_ROLE_TASK_PERMISSIONS="ARN" 
// https://docs.aws.amazon.com/AmazonECS/latest/developerguide/task_execution_IAM_role.html
- AWS_IAM_ROLE_NAME_TASK="ARN"
- AWS_SECURITY_GROUP=""


## Links
- https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup-project-maven.html
- https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/ecs/src/main/java/com/example/ecs# java-aws-ecs-app-iac

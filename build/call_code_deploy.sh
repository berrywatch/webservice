aws deploy create-deployment --application-name csye6225-webapp \
--deployment-config-name CodeDeployDefault.AllAtOnce \
--deployment-group-name csye6225-webapp-deployment \
--description "CSYE6225 - CodeDeploy" \
--s3-location bucket=codedeploy.kanzhang.me,key=webapp.zip,bundleType=zip \
--profile=dev --output json 
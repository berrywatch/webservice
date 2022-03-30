aws deploy push \
  --application-name csye6225-webapp \
  --description "This is a revision for the web application" \
  --ignore-hidden-files \
  --s3-location s3://codedeploy.kanzhang.me/webapp.zip \
  --source ./upload \
  --profile=dev
sudo systemctl stop registration
sudo chmod +x /var/webapp/Registration-0.0.1-SNAPSHOT.jar
sudo mv /var/webapp/registration.service /etc/systemd/system
sudo mv /var/webapp/cloudwatch-agent.json /opt/cloudwatch-agent.json

sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl \
    -a fetch-config \
    -m ec2 \
    -c file:/opt/cloudwatch-agent.json \
    -s

sudo systemctl start amazon-cloudwatch-agent
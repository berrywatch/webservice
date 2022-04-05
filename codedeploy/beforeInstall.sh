sudo systemctl stop registration
sudo systemctl stop amazon-cloudwatch-agent
# remove all previous files except application.properties
find /var/webapp ! -name 'application.properties' -type f -exec rm -f {} +
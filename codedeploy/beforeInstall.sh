sudo systemctl stop registration
# remove all previous files except application.properties
find /var/webapp ! -name 'application.properties' -type f -exec rm -f {} +
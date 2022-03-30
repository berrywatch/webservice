sudo systemctl stop registration
sudo chmod +x /var/webapp/Registration-0.0.1-SNAPSHOT.jar
sudo mv /var/webapp/registration.service /etc/systemd/system

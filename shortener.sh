#!/bin/bash

echo "Installing MariaDB..."
# Install MariaDB
apt-get update
apt-get install -y mariadb-server

echo "Starting MariaDB service..."
# Start MariaDB service
service mariadb start

echo "Waiting for MariaDB to start (adjust sleep time as needed)..."

# Wait for MariaDB to start
while ! mysqladmin ping -hlocalhost -uroot -p'YOUR_PASSWORD' --silent; do
    echo "MariaDB is not yet available. Waiting..."
    sleep 5
done

echo "Running SQL script to initialize the database..."
# Access MySQL Command Line and run SQL script to initialize the database
mysql -u root -e "source /usr/local/tomcat/create.sql"

echo "Displaying databases and tables..."
# Display the databases and tables
mysql -u root -e "SHOW DATABASES; USE shortener; SHOW TABLES;"

echo "Altering user and reloading privileges..."
# Run SQL commands to alter user and reload privileges
mysql -u root <<EOF
ALTER USER 'root'@'localhost' IDENTIFIED VIA mysql_native_password USING PASSWORD('1234qwer');
FLUSH PRIVILEGES;
EOF

echo "Starting Tomcat in the background..."
# Start Tomcat in the background
sh /usr/local/tomcat/bin/catalina.sh run

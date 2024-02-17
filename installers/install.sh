#!/bin/bash

# Set Tomcat and URL variables
TOMCAT_VERSION="10.0.0-M15"
TOMCAT_URL="https://archive.apache.org/dist/tomcat/tomcat-10/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz"
WAR_URL="https://github.com/aamitn/URLShortener/releases/download/final/shortener.war"
SERVER_XML_CONTEXT="<Context path=\"\" docBase=\"shortener\" debug=\"0\" reloadable=\"true\"></Context>"
SQL_FILE_URL="https://github.com/aamitn/URLShortener/raw/master/create.sql"

# Function to download and extract Tomcat
download_and_extract_tomcat() {
    echo "Downloading Tomcat..."
    curl -O "${TOMCAT_URL}"
    tar -xzvf "apache-tomcat-${TOMCAT_VERSION}.tar.gz"
    rm "apache-tomcat-${TOMCAT_VERSION}.tar.gz"
}

# Function to download the WAR file
download_war_file() {
    echo "Downloading shortener.war..."
    curl -LJO "${WAR_URL}"
}

# Function to configure Tomcat and deploy the WAR file
configure_and_deploy() {
    echo "Configuring and deploying..."
    # Copy the WAR file to Tomcat webapps directory
    cp shortener.war apache-tomcat-${TOMCAT_VERSION}/webapps/

    # Add Context to server.xml
    echo "${SERVER_XML_CONTEXT}" >> apache-tomcat-${TOMCAT_VERSION}/conf/server.xml
}

# Function to download and run MariaDB server and execute SQL file
download_and_configure_mariadb() {
    echo "Downloading and configuring MariaDB..."
    apt-get update
    apt-get install -y mariadb-server
    service mariadb start
    sleep 10

    # Set root password
    mysql -u root -e "ALTER USER 'root'@'localhost' IDENTIFIED VIA mysql_native_password USING PASSWORD('$ROOT_PASSWORD'); FLUSH PRIVILEGES;"

    # Create 'shortener' database
    mysql -u root -p"$ROOT_PASSWORD" -e "CREATE DATABASE IF NOT EXISTS shortener;"

    # Download SQL file
    wget -O create.sql "$SQL_FILE_URL"

    # Run the SQL script
    mysql -u root -p"$ROOT_PASSWORD" shortener < create.sql

    # Optionally, remove the downloaded SQL file
    rm create.sql
}

# Main Script Execution
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    # Linux specific commands
    download_and_extract_tomcat
    download_war_file
    configure_and_deploy
    download_and_configure_mariadb

elif [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" ]]; then
    # Windows specific commands
    echo "Windows is not currently supported for this script."
    exit 1

else
    echo "Unsupported operating system."
    exit 1

fi

echo "Local deployment completed successfully!"
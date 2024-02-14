# syntax=docker/dockerfile:1

# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Clone the repository
RUN git clone https://github.com/aamitn/URLShortener.git

WORKDIR /URLShortener

# Build the application
RUN mvn clean install

# Stage 2: Create the final image
FROM tomcat:10-jdk21-openjdk-slim

# Set environment variables
ENV CATALINA_BASE /usr/local/tomcat
ENV CATALINA_HOME /usr/local/tomcat
ENV PATH $CATALINA_HOME/bin:$PATH

# Copy the WAR file from the builder stage
COPY target/shortener.war $CATALINA_BASE/webapps/


# Add configuration for document base path
COPY server.xml $CATALINA_BASE/conf/server.xml


# Expose ports
EXPOSE 8080
EXPOSE 3306


# Copy the startup script
COPY shortener.sh /usr/local/tomcat/shortener.sh

# Copy the sql file
COPY create.sql /usr/local/tomcat/create.sql

# Grant execute permissions to the startup.sh script
RUN chmod +x /usr/local/tomcat/shortener.sh

# Start Tomcat and MariaDB using the startup script
CMD ["sh", "/usr/local/tomcat/shortener.sh"]



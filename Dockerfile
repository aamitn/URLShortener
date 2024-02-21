#syntax=docker/dockerfile:1

# Add the following lines to tag the image (replace 'your_username' and 'shortener-app' with your Docker Hub username and repository name)
ARG VERSION=latest
ARG IMAGE_NAME=nmpl/shortener
ARG TAG=$VERSION



#
#------STAGE 1: Build the application-----#
#
# Get Maven with JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS builder

#Cloud Install :  Clone the repository
RUN git clone https://github.com/aamitn/URLShortener.git

#Local Install
#ADD . /UrlShortener

# Change working directory to the repo directory
WORKDIR /URLShortener

# Docker makes db accessible like this : mysql://<container-name>:port instead of mysql://<server-ip>:port
# Example real world db access url : mysql://127.0.0.1:3306
# Example Docker  db access url : mysql://database:3306 (container name is datbase)
# Change the database ip in app config to the database docker container name/service
RUN sed -i "s|database.ip=127.0.0.1|database.ip=db |g" src/main/resources/application.properties

# Build the application
RUN mvn clean install



#
#------STAGE 2: Deploy the Generated War-----#
#
## Get Tomacat 10 with JDK21
FROM tomcat:10-jdk21-openjdk-slim

# Set environment variables
ENV CATALINA_BASE /usr/local/tomcat
ENV CATALINA_HOME /usr/local/tomcat
ENV PATH $CATALINA_HOME/bin:$PATH

# Copy the WAR file from the builder stage
COPY --from=builder /URLShortener/target/shortener.war  $CATALINA_BASE/webapps/



#
#------STAGE 3: Configure and Start Application Server-----#
#
# Add configuration for document base path
COPY server.xml $CATALINA_BASE/conf/server.xml

# Expose ports
EXPOSE 8080

# Start Tomcat and MariaDB using the startup script
CMD ["catalina.sh", "run"]
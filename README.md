## <img src="https://i.ibb.co/4MFScFw/tusc-logo.png" width="20%" alt=""> **TUSC - The URL Shortener Company**

[![buddy pipeline](https://app.buddy.works/amitnandileo-2/urlshortener/pipelines/pipeline/483649/badge.svg?token=fb16d4ceb25aaee899aeebeb8f9d57239dd800defc2afb65a0176c36d32bd464)](https://app.buddy.works/amitnandileo-2/urlshortener/pipelines/pipeline/483649)  
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE.md)[![GitHub last commit](https://img.shields.io/github/last-commit/aamitn/URLShortener.svg)](https://github.com/aamitn/URLShortener/commits/main)[![GitHub issues](https://img.shields.io/github/issues/aamitn/URLShortener.svg)](https://github.com/aamitn/URLShortener/issues)[![GitHub pull requests](https://img.shields.io/github/issues-pr/aamitn/URLShortener.svg)](https://github.com/aamitn/URLShortener/pulls)[![GitHub contributors](https://img.shields.io/github/contributors/aamitn/URLShortener.svg)](https://github.com/aamitn/URLShortener/graphs/contributors)  
[![Twitter](https://img.shields.io/twitter/url/https/github.com/yourusername/your-repo.svg?style=social)](https://twitter.com/intent/tweet?url=https%3A%2F%2Fgithub.com%2Faamitn%2FURLShortener&text=Check%20out%20this%20awesome%20URL%20Shortener%20and%20Bio%20Page%20Application&via=yourtwitterhandle)
[![Docker Image Size](https://badgen.net/docker/size/trueosiris/godaddypy?icon=docker&label=image%20size)](https://hub.docker.com/r/nmpl/shortener)

An enterprise-grade, powerful and scalable URL shortener with integrated bio pages built using Spring Boot.

<table><tbody><tr><td>Maven Package</td><td><a href="https://github.com/aamitn/URLShortener/packages/2069528">Github Packages - com.bitmutex.shortener</a></td></tr><tr><td>Docker Image</td><td><a href="https://hub.docker.com/r/nmpl/shortener">DockerHub - nmpl/shortener:latest</a></td></tr></tbody></table>

## Table of Contents

*   [Overview](#overview)
*   [Features](#features)
*   [Getting Started](#getting-started)
    *   [Prerequisites](#prerequisites)
    *   [Local Setup](#local-setup)
    *   [Deployment](#deployment)
*   [Usage](#usage)
*   [Contributing](#contributing)
*   [License](#license)

## Overview

This project is an enterprise-grade URL shortener and bio page application developed with Spring Boot. It provides a robust solution for shortening URLs and creating bio pages for users. The application is designed for scalability and includes features such as analytics, user management, and subscription plans.

## Features

*   Shorten URLs and create custom short URLs.
*   Integrated bio pages for users.
*   Analytics tracking for each shortened URL.
*   User management with registration, login, and profile updates.
*   Subscription plans for premium features.
*   Rate limiting to prevent abuse.
*   Forgot password and username recovery functionality.
*   OPEN API 3.0 Complaint REST API set, with extensive docs.

## Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

*   Java (JDK 11 or later)
*   Maven
*   MySQL
*   Your favorite IDE (IntelliJ, Eclipse, etc.)

### Local Setup

1.  Clone the repository:

    ```plaintext
    git clone https://github.com/aamitn/URLShortener.git
    ```

2.  **Apply the Database Schema:**

    Execute the provided **db\_schema.sql** file to set up the necessary tables and schema for the application. This script will also create the required database.

    ```plaintext
    mysql -u your_username -p < path/to/db_schema.sql
    ```

3.  Update the application.properties file:

    Update the application.properties file in the src/main/resources directory with your database configuration.

4.  Build and run the application:

    ```plaintext
       cd URLShortener
       mvn spring-boot:run
    ```

5.  Access the application at http://localhost:8080

### Deployment

1.  **Build a deployable WAR:**

    ```plaintext
    mvn clean install
    ```

2.  **Deploy the WAR:**
    *   **Option 1: Manual Deployment**

        Deploy the generated WAR file to your Tomcat server. Copy the WAR file to the `webapps` directory of your Tomcat installation.

        ```plaintext
        cp target/shorten.war /path/to/tomcat/webapps/
        ```

    *   **Option 2: Web Interface Upload**

        Alternatively, if your Tomcat server provides a web interface for WAR file deployment, follow these steps:

        *   Access the Tomcat Manager web interface at `http://localhost:8080/manager/html` (replace with your Tomcat server address).
        *   Log in with your Tomcat manager credentials.
        *   Navigate to the "WAR file to deploy" section.
        *   Choose the `shorten.war` file using the file upload button.
        *   Click the "Deploy" button.
3.  **Configure environment variables:**

    Set environment variables for cloud-specific settings.

4.  **Adjust Tomcat Configuration (Important):**

    To ensure proper functionality, it's important to run the application on the root of the servlet container. If you're using Tomcat, add the following line to your `server.xml` configuration file within the `<Host>` section:

    ```xml
    <Context path="" docBase="shorten" reloadable="true"></Context>
    ```

    This ensures that the application runs on the root context path. Adjust the `docBase` attribute according to your deployment directory.

5.  **Start Tomcat:**

    Start your Tomcat server.

    ```plaintext
    /path/to/tomcat/bin/startup.sh   # for Linux
    /path/to/tomcat/bin/startup.bat  # for Windows
    ```

    Access the application at `http://localhost:8080/`.

6.  **Adjust Configuration (Optional):** If you need to customize the application configuration after deployment, you can find the **application.properties** file within the deployed WAR file. THe file can be accesses as :

    ```plaintext
    vi /path/to/tomcat/webapps/your-app/WEB-INF/classes/application.properties
    ```


### Cloud Native Deployment

To deploy the URL Shortener application on your cloud environment, follow the steps below:

**Prerequisites**

*   [Docker](https://www.docker.com/get-started)
*   [Docker Compose](https://docs.docker.com/compose/install/)

1.  **Clone the Repository:**

    ```plaintext
    git clone https://github.com/your-username/URLShortener.git
    cd URLShortener
    ```

2.  **Build and Run Your Own Image:**

    ```plaintext
    docker build -t shortener:latest .
    docker run -p 8080:8080 -p 3306:3306 shortener:latest
    ```

3.  **Use Our Pre-built Image with Docker Compose:**

    ```plaintext
    docker compose build
    docker compose up
    ```

4.  **Customizing Docker Compose Configuration:**

    In the **docker-compose.yml** file, you can customize the build source for the Shortener service:

    ```plaintext
        #Build from docker hub image .Comment/Uncomment Below
        image: nmpl/shortener:latest
    
     #Build from local Dockerfile.Comment/Uncomment Below
     #   build:
     #     context: .
     #     dockerfile: Dockerfilekerfile
    ```

    Comment or uncomment the relevant lines based on whether you want to use the pre-built image from Docker Hub or build from the local Dockerfile.


5.  **Deploy Using K8s**

*   **Setup Kubernetes Deployment and Service:**

     ```plaintext
     kubectl apply -f shortener-deployment.yaml
     kubectl apply -f shortener-service.yaml        
    ```

*    **Use the following command to monitor the deployment:**

    ```plaintext
     kubectl get pods
    ```

Wait until the pod is in the "Running" state.

*   **Access the Application**

    Depending on your Kubernetes setup, you might need to get the external IP of the service:

    ```plaintext
     kubectl get service shortener-service
    ```

    Access your application using the provided external IP.



### App Health

*   Check application status from the /monitoring page example http://localhost:8080/monitoring

### Usage Cases

1.  Shorten URLs by visiting the URL Shortener page.
2.  Access analytics for each shortened URL.
3.  Create and manage bio pages in the Bio section.

### Contributing

Contributions are welcome! Please follow the contribution guidelines.

### License

This project is licensed under the MIT License - see the LICENSE.md file for details.
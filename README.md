## Enterprise-Grade URL Shortener and Bio Page Application

[![Build Status](https://travis-ci.org/yourusername/your-repo.svg?branch=main)](https://travis-ci.org/yourusername/your-repo)[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE.md)[![GitHub last commit](https://img.shields.io/github/last-commit/yourusername/your-repo.svg)](https://github.com/yourusername/your-repo/commits/main)[![GitHub issues](https://img.shields.io/github/issues/yourusername/your-repo.svg)](https://github.com/yourusername/your-repo/issues)[![GitHub pull requests](https://img.shields.io/github/issues-pr/yourusername/your-repo.svg)](https://github.com/yourusername/your-repo/pulls)[![GitHub contributors](https://img.shields.io/github/contributors/yourusername/your-repo.svg)](https://github.com/yourusername/your-repo/graphs/contributors)  
[![Twitter](https://img.shields.io/twitter/url/https/github.com/yourusername/your-repo.svg?style=social)](https://twitter.com/intent/tweet?url=https%3A%2F%2Fgithub.com%2Fyourusername%2Fyour-repo&text=Check%20out%20this%20awesome%20URL%20Shortener%20and%20Bio%20Page%20Application&via=yourtwitterhandle)

A powerful and scalable URL shortener with integrated bio pages built using Spring Boot.

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

*   Shorten URLs and create custom short URLs
*   Integrated bio pages for users
*   Analytics tracking for each shortened URL
*   User management with registration, login, and profile updates
*   Subscription plans for premium features
*   Rate limiting to prevent abuse
*   Forgot password and username recovery functionality
*   ...

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
    git clone https://github.com/yourusername/your-repo.git
    ```
    
2.  **Apply the Database Schema:**
    
    Execute the provided **db\_schema.sql** file to set up the necessary tables and schema for the application. This script will also create the required database.
    
3.  ```plaintext
    mysql -u your_username -p < path/to/db_schema.sql
    ```
    
4.  Update the application.properties file:

> Update the application.properties file in the src/main/resources directory with your database configuration.

1.  Build and run the application:
    
    ```plaintext
       Ccd your-repo
       mvn spring-boot:run
    ```
    
2.  Access the application at http://localhost:8080

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
    

### Usage

1.  Shorten URLs by visiting the URL Shortener page.
2.  Access analytics for each shortened URL.
3.  Create and manage bio pages in the Bio section.

### Contributing

Contributions are welcome! Please follow the contribution guidelines.

### License

This project is licensed under the MIT License - see the LICENSE.md file for details.

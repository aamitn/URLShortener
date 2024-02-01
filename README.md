# Enterprise-Grade URL Shortener and Bio Page Application

[![Build Status](https://travis-ci.org/yourusername/your-repo.svg?branch=main)](https://travis-ci.org/yourusername/your-repo)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE.md)
[![GitHub last commit](https://img.shields.io/github/last-commit/aamitn/URLShortener.svg)](https://github.com/aamitn/URLShortener/commits/main)
[![GitHub issues](https://img.shields.io/github/issues/aamitn/URLShortener.svg)](https://github.com/aamitn/your-repo/issues)
[![GitHub pull requests](https://img.shields.io/github/issues-pr/aamitn/URLShortener.svg)](https://github.com/aamitn/URLShortener/pulls)


A powerful and scalable URL shortener with integrated bio pages built using Spring Boot.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Local Setup](#local-setup)
  - [Deployment](#deployment)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Overview

This project is an enterprise-grade URL shortener and bio page application developed with Spring Boot. It provides a robust solution for shortening URLs and creating bio pages for users. The application is designed for scalability and includes features such as analytics, user management, and subscription plans.

## Features

- Shorten URLs and create custom short URLs
- Integrated bio pages for users
- Analytics tracking for each shortened URL
- User management with registration, login, and profile updates
- Subscription plans for premium features
- Rate limiting to prevent abuse
- Forgot password and username recovery functionality
- ...

## Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

- Java (JDK 11 or later)
- Maven
- MySQL
- Your favorite IDE (IntelliJ, Eclipse, etc.)

### Local Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/yourusername/your-repo.git
   
2. Create a MySQL database:

   ```sql
   CREATE DATABASE url_shortener_db;

2. Update the application.properties file:

>Update the application.properties file in the src/main/resources directory with your database configuration.


4. Build and run the application:

   ```bash
      Ccd your-repo
      mvn spring-boot:run

5. Access the application at http://localhost:8080

### Deployment

1. Build a deployable JAR:

   ```bash
   mvn clean install
   
2. Deploy the JAR:

>Deploy the generated JAR file to your cloud provider of choice (AWS, GCP, Azure, etc.).

3.Configure environment variables:

>Set environment variables for cloud-specific settings.


4. Start the application on the cloud server:

   ```bash
   java -jar your-repo.jar

### Usage
1. Shorten URLs by visiting the URL Shortener page.
2. Access analytics for each shortened URL.
3. Create and manage bio pages in the Bio section.

### Contributing
Contributions are welcome! Please follow the contribution guidelines.

### License
This project is licensed under the MIT License - see the LICENSE.md file for details.

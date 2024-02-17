@echo off

REM Set Tomcat and URL variables
set "TOMCAT_VERSION=10.1.9"
set "TOMCAT_URL=https://archive.apache.org/dist/tomcat/tomcat-10/v%TOMCAT_VERSION%/bin/apache-tomcat-%TOMCAT_VERSION%-windows-x64.zip"
set "WAR_URL=https://github.com/aamitn/URLShortener/releases/download/final/shortener.war"
set "SQL_FILE_URL=https://github.com/aamitn/URLShortener/raw/master/create.sql"
set "SERVER_XML_URL=https://raw.githubusercontent.com/aamitn/URLShortener/master/server.xml"
REM Set WildFly and URL variables
set "WILDFLY_VERSION=31.0.0.Final"
set "WILDFLY_URL=https://github.com/wildfly/wildfly/releases/download/%WILDFLY_VERSION%/wildfly-%WILDFLY_VERSION%.zip"
set "WAR_URL=https://github.com/aamitn/URLShortener/releases/download/final/shortener.war"


setlocal enabledelayedexpansion

REM Ask the user for deployment target
echo Choose the deployment target:
echo 1. Tomcat
echo 2. WildFly
set /p DEPLOYMENT_TARGET=Enter the number (default is Tomcat):

REM Set default deployment target to Tomcat if user input is empty
if not defined DEPLOYMENT_TARGET set "DEPLOYMENT_TARGET=1"

REM Function to download the WAR file
:download_war_file
echo Downloading shortener.war...
curl -LJO "%WAR_URL%"

REM Function to deploy the WAR file
:deploy_war
echo Deploying shortener.war to %DEPLOYMENT_TARGET%...
if "%DEPLOYMENT_TARGET%"=="1" (
setlocal enabledelayedexpansion

REM Function to download and extract Tomcat
:download_and_extract_tomcat
echo Downloading Tomcat...
curl -O "%TOMCAT_URL%"
PowerShell Expand-Archive "apache-tomcat-%TOMCAT_VERSION%-windows-x64.zip" -DestinationPath .
del "apache-tomcat-%TOMCAT_VERSION%-windows-x64.zip"


REM Function to configure Tomcat and deploy the WAR file
:configure_and_deploy
echo Configuring and deploying...
copy shortener.war "apache-tomcat-%TOMCAT_VERSION%\webapps\"

REM Downloading the server.xml file
echo Downloading server.xml...
curl -LJO "%SERVER_XML_URL%"

REM Replace the existing server.xml with the downloaded one
echo Replacing server.xml...
copy /Y server.xml "apache-tomcat-%TOMCAT_VERSION%\conf\server.xml"

REM Run Tomcat server using startup.bat
:run_tomcat
echo Running Tomcat server...
cd "apache-tomcat-%TOMCAT_VERSION%\bin"
call startup.bat
REM Wait for Tomcat to start (adjust sleep time as needed)
timeout /t 30 /nobreak
call shutdown.bat
timeout /t 2 /nobreak
call startup.bat
cd ..
cd ..

) else if "%DEPLOYMENT_TARGET%"=="2" (
@echo off
setlocal enabledelayedexpansion


REM Function to download and extract WildFly
:download_and_extract_wildfly
echo Downloading WildFly...
curl -LJO "%WILDFLY_URL%"
PowerShell Expand-Archive "wildfly-%WILDFLY_VERSION%.zip" -DestinationPath .
del "wildfly-%WILDFLY_VERSION%.zip"



REM Function to deploy the WAR file
:deploy_war
echo Deploying shortener.war to WildFly...
copy shortener.war "wildfly-%WILDFLY_VERSION%\standalone\deployments\"

REM Run WildFly server
:run_wildfly
echo Running WildFly...
cd "wildfly-%WILDFLY_VERSION%\bin"
dir
start standalone.bat -c standalone-full.xml


echo WildFly deployment completed successfully!
cd..
cd..
timeout /t 3 /nobreak

) else (
    echo Invalid deployment target selected.
    exit /b 1
)


echo Deployment Stage 1 Done


REM Function to download and install MariaDB
REM Check if MariaDB is already installed
if not exist mariadb-11.4.0-winx64 (
    echo Downloading MariaDB installer...
    curl -O "https://mirrors.aliyun.com/mariadb//mariadb-11.4.0/winx64-packages/mariadb-11.4.0-winx64.zip"

    echo Installing MariaDB...
    PowerShell Expand-Archive "mariadb-11.4.0-winx64.zip" -DestinationPath .
    del "mariadb-11.4.0-winx64.zip"
)

cd mariadb-11.4.0-winx64/bin
@echo SET PASSWORD FOR 'root'@'localhost' = PASSWORD('1234qwer');> dbinit.txt
cd..
cd..


REM Navigate to mariadb-11.4.0-winx64/bin
cd mariadb-11.4.0-winx64/bin

REM Get the current directory
set "CURRENT_DIR=%CD%"

REM Initialize DB
call mariadb-install-db.exe

REM Run MariaDB server with the init file
start mysqld.exe --console --init-file="%CURRENT_DIR%\\dbinit.txt"

REM Create DB
timeout /t 1 /nobreak
mysql -u root -p1234qwer -e "CREATE DATABASE IF NOT EXISTS shortener;"
curl -LJO "https://github.com/aamitn/URLShortener/raw/master/create.sql"
mysql -u root -p1234qwer < create.sql
mysql -u root -p1234qwer -e "SHOW ENGINE PERFORMANCE_SCHEMA STATUS;SHOW ENGINE INNODB STATUS;"
mysql -u root -p1234qwer -e "SHOW DATABASES;"
mysql -u root -p1234qwer -e "USE shortener; SHOW TABLES; SHOW TABLE STATUS\G;"
del create.sql

REM start browser
echo Application Deployed Successfully...
start "" http://localhost:8080
timeout /t 20 /nobreak
exit /b

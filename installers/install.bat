@echo off

setlocal enabledelayedexpansion

REM Set Tomcat and URL variables
set "TOMCAT_VERSION=10.1.9"
set "TOMCAT_URL=https://archive.apache.org/dist/tomcat/tomcat-10/v%TOMCAT_VERSION%/bin/apache-tomcat-%TOMCAT_VERSION%-windows-x64.zip"
set "WAR_URL=https://github.com/aamitn/URLShortener/releases/download/WAR/shortener.war"
set "SQL_FILE_URL=https://github.com/aamitn/URLShortener/raw/master/create.sql"
set "SERVER_XML_URL=https://raw.githubusercontent.com/aamitn/URLShortener/master/server.xml"

REM Function to download and extract Tomcat
:download_and_extract_tomcat
echo Downloading Tomcat...
curl -O "%TOMCAT_URL%"
PowerShell Expand-Archive "apache-tomcat-%TOMCAT_VERSION%-windows-x64.zip" -DestinationPath .
del "apache-tomcat-%TOMCAT_VERSION%-windows-x64.zip"

REM Function to download the WAR file
:download_war_file
echo Downloading shortener.war...
curl -LJO "%WAR_URL%"

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



REM Main Script Execution
if "%OS%"=="Windows_NT" (
    call :download_and_install_mariadb
) else (
    echo Unsupported operating system.
    exit /b 1
)

echo Local deployment completed successfully!
exit /b 0

REM Function to download and install MariaDB
:download_and_install_mariadb
echo Downloading MariaDB installer...
curl -O "https://mirror.docker.ru/mariadb//mariadb-11.4.0/winx64-packages/mariadb-11.4.0-winx64.zip"

echo Installing MariaDB...
PowerShell Expand-Archive "mariadb-11.4.0-winx64.zip" -DestinationPath .
del "mariadb-11.4.0-winx64.zip"

cd mariadb-11.4.0-winx64/bin
cd..
cd..

@echo SET PASSWORD FOR 'root'@'localhost' = PASSWORD('1234qwer');> init.txt



REM Get the current directory
set "CURRENT_DIR=%CD%"

start db.bat
cd mariadb-11.4.0-winx64/bin
REM Initialize DB
call mariadb-install-db.exe 
REM Run MariaDB server with the init file
call mysqld.exe --console --init-file="%CURRENT_DIR%\\init.txt"




set "SQL_FILE_URL=https://github.com/aamitn/URLShortener/raw/master/create.sql"

echo Waiting for MariaDB to start...
timeout /t 1 /nobreak

cd mariadb-11.4.0-winx64/bin

REM Run SQL commands using mysql command-line tool
mysql -u root -p1234qwer -e "CREATE DATABASE IF NOT EXISTS shortener;"

REM Download SQL file
curl -LJO "%SQL_FILE_URL%"

REM Run the SQL script
mysql -u root -p1234qwer < create.sql

mysql -u root -p1234qwer -e "SHOW DATABASES;"
mysql -u root -p1234qwer -e "USE shortener"
mysql -u root -p1234qwer -e "SELECT * FROM shortener;"


REM Optionally, remove the downloaded SQL file
del create.sql

echo Deployed Successfully...
start "" http://localhost:8080
exit /b 0
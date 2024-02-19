/**
 * @file  gateway.php
 * @brief Url Shortener Application
 *         Github Repository : https://github.com/aamitn/URLShortener
 * JAVA 21 / Spring Boot 3
 *
 * @author    Amit Kumar Nandi (Bitmutex Technologies) <amit@bitmutex.com>
 * @version   1.0.5
 * @date      2023-03-30
 * @since     2022-09-10
 * @copyright (c) 2023-2024 Bitmutex Technologies
 * @copyright GNU Lesser General Public License
 *
 *********************************************************************/

package com.bitmutex.shortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
//@ComponentScan(basePackages = "com.bitmutex.shortener")
public class UrlShortenerApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
       SpringApplication.run(UrlShortenerApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder){
        return builder.sources(UrlShortenerApplication.class);
    }
}

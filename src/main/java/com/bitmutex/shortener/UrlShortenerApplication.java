package com.bitmutex.shortener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@ComponentScan(basePackages = "com.bitmutex.shortener")
public class UrlShortenerApplication {
    public static void main(String[] args) {
       SpringApplication.run(UrlShortenerApplication.class, args);
    }
}

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

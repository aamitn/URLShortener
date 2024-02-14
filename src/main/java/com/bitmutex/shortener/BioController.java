package com.bitmutex.shortener;

// BioController.java

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class BioController {

    private final UrlShortenerService urlShortenerService;

    @Autowired
    public BioController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @GetMapping("/bio")
    public String showBioEditPage() {
        return "bio";
    }
}

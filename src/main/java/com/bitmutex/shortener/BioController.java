package com.bitmutex.shortener;

// BioController.java

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

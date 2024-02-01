package com.bitmutex.shortener;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UrlDetailsController {
    UrlShortenerService urlShortenerService;

    @GetMapping("/details")
    public String getUrlDetails(@RequestParam String shortUrl, Model model) {

        // Add the details to the model
        model.addAttribute("shortUrl", shortUrl);

        // Return the name of the details HTML file (without extension)
        return "details";
    }
}
package com.bitmutex.shortener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/api/status")
public class StatusCheckController {


    private final StatusCheckService statusCheckService;

    @Autowired
    public StatusCheckController(StatusCheckService statusCheckService) {
        this.statusCheckService = statusCheckService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Integer>> getServerStatus() {
        Map<String, Integer> status = statusCheckService.getServerStatus();
        return ResponseEntity.ok(status);
    }
}
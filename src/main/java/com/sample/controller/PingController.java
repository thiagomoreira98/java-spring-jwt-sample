package com.sample.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
public class PingController {

    @GetMapping("/ping")
    public ResponseEntity<Object> list() {
        var response = String.format("{ \"date\": \"%s\" }", LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

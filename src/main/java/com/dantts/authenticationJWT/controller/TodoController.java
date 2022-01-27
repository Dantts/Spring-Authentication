package com.dantts.authenticationJWT.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/todo")
public class TodoController {

    @GetMapping
    public String seila(){
        return "VAI TRABALHAR";
    }

    @GetMapping("/seila")
    public String seila2(){
        return "VAI TRABALHAR 22222";
    }

    @PostMapping("/seila")
    public String postSeila() {
        return "POST MAPPING";
    }
}

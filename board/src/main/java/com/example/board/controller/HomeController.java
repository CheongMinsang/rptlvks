package com.example.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


// WebからのRequestを処理するController
@Controller
public class HomeController {

    @GetMapping("/")
    public String home(){
        return "index";
    }
}

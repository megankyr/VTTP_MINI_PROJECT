package com.ssf.mini.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MovieController {

     @GetMapping("/search")
    public String displaySearchOptions() {
        return "search";
    }

    @GetMapping("/search/title")
    public String searchTitle() {
        return "title";
    }
    
}
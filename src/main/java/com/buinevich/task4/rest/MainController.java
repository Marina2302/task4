package com.buinevich.task4.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/login")
    public String login() {
        return "login_view";
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }
}

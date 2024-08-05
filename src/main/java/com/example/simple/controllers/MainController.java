package com.example.simple.controllers;

import com.example.simple.models.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("user", user);
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model, @AuthenticationPrincipal User user) {
        if (user != null) {
            model.addAttribute("user", user);
        }
        model.addAttribute("title", "О нас");
        return "about";
    }
}

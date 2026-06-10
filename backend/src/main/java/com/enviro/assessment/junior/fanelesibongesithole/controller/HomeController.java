package com.enviro.assessment.junior.fanelesibongesithole.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/pages/landing.html";
    }

    @GetMapping({
            "/landing.html",
            "/login.html",
            "/index.html",
            "/investment.html",
            "/withdrawals.html",
            "/compliance.html",
            "/reports.html",
            "/profile.html"
    })
    public String legacyPages(HttpServletRequest request) {
        String filename = request.getRequestURI().substring(request.getRequestURI().lastIndexOf('/') + 1);
        return "redirect:/pages/" + filename;
    }
}

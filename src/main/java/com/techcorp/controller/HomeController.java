package com.techcorp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Prosty kontroler strony głównej.
 * Zapobiega błędowi "No static resource ." przy żądaniu na root '/'
 * przekierowując użytkownika do listy pracowników.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String root() {
        // Możesz zmienić docelowy adres na "/statistics" jeśli wolisz dashboard
        return "redirect:/employees";
    }
}

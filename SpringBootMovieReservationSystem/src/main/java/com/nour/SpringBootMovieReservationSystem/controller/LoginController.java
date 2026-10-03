package com.nour.SpringBootMovieReservationSystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/showCustomerLoginPage")
    public String showCustomerLoginPage() {
        return "customer-login";
    }

    @GetMapping("/showAccessedDeniedPage")
    public String showAccessDenied() {
        return "access-denied";
    }

    @GetMapping("/showAdminLoginPage")
    public String showAdminLoginPage() {
        return "admin-login";
    }
}

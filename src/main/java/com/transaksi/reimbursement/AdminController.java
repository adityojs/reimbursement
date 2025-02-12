package com.transaksi.reimbursement;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @GetMapping("/login-admin")
    public String showAdminLoginPage() {
        return "login-admin";
    }

    @PostMapping("/login-admin")
    public String processAdminLogin(@ModelAttribute LoginRequest loginRequest, Model model) {
        if ("admin".equals(loginRequest.getUsername()) && "adminpass".equals(loginRequest.getPassword())) {
            model.addAttribute("message", "Welcome Admin!");
            return "admin-dashboard";
        } else {
            model.addAttribute("error", "Invalid credentials");
            return "login-admin";
        }
    }
}

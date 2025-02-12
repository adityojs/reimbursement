package com.transaksi.reimbursement;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping("/login-user")
    public String showUserLoginPage() {
        return "login-user";
    }

    @PostMapping("/login-user")
    public String processUserLogin(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        if ("user".equals(username) && "userpass".equals(password)) {
            session.setAttribute("userLoggedIn", true); // Mark user as logged in
            return "redirect:/reimbursement"; // Redirect to reimbursement page
        } else {
            model.addAttribute("error", "Invalid credentials");
            return "login-user";
        }
    }
}


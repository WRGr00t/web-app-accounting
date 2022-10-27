package com.example.webappaccounting.controller;

import com.example.webappaccounting.model.Role;
import com.example.webappaccounting.model.User;
import com.example.webappaccounting.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;
import java.util.Map;

@Controller
public class RegController {
    @Autowired
    private UserRepo userRepo;

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Map<String, Object> model) {
        User userFromDB = userRepo.findByUsername(user.getUsername());
        if (userFromDB != null) {
            model.put("message", "User exists(");
            return "registration";
        }
        if (user.getUsername().isEmpty() || user.getPassword().isEmpty()) {
            model.put("message", "All inputs must be not empty");
            return "registration";
        }
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        userRepo.save(user);

        return "redirect:/login";
    }
}

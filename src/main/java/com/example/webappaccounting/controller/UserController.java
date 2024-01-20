package com.example.webappaccounting.controller;

import com.example.webappaccounting.model.Role;
import com.example.webappaccounting.model.Subscribe;
import com.example.webappaccounting.model.User;
import com.example.webappaccounting.service.ParseHelper;
import com.example.webappaccounting.service.SubscribeService;
import com.example.webappaccounting.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private ParseHelper helper;

    @Autowired
    private SubscribeService subscribeService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public String userList(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("{user}")
    public String userEditForm(@PathVariable User user, Model model) {

        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        model.addAttribute("subpage", true);
        return "userEdit";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public String userSave(@RequestParam String username,
                           @RequestParam(required = false) String email,
                           @RequestParam Map<String, String> form,
                           @RequestParam("userId") User user){

            userService.saveUser(user, username, email, form);

        return "redirect:/user";
    }

    @GetMapping("profile")
    public String getProfile(@RequestParam(name="person", required=false) String person,
                             Map<String, Object> model,
                             @AuthenticationPrincipal User user) {

        Optional<User> userInDB = userService.findById(user.getId());

        if (userInDB.isPresent()) {
            user = userInDB.get();
        }
        HashSet<String> persons = (HashSet<String>) helper.getNameInRangeWithout85(
                LocalDate.now().minusMonths(2),
                LocalDate.now());
        ArrayList<String> names = (ArrayList<String>) persons.stream()
                .sorted()
                .collect(Collectors.toList());

        if (user.getEmail() != null) {
            ArrayList<Subscribe> list = (ArrayList<Subscribe>) subscribeService.findByMail(user.getEmail());
            if (!list.isEmpty()) {
                model.put("subscribes", list);
            } else {
                model.put("message1", "Нет подписок");
            }

        } else {
            model.put("message2", "Сначала установите e-mail");
        }
        if (person == null) {
            person = "";
        }
        if (user.getEmail() != null || !user.getEmail().isEmpty()) {
            model.put("mail", user.getEmail());
        } else {
            model.put("mail", "Не установлен");
        }
        if (user.getRoles().contains(Role.ADMIN)) {
            model.put("admin", true);
        }

        model.put("select", person);
        model.put("user", user);
        model.put("subpage", true);
        model.put("persons", names);

        return "profile";
    }

    @PostMapping("profile")
    public String updateProfile(@AuthenticationPrincipal User user,
                                @RequestParam(required = false) String username,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false) String nameForSubscribe,
                                @RequestParam(required = false) String password) {
        userService.updateProfile(user, username, email, nameForSubscribe, password);

        return "redirect:/user/profile";
    }

}

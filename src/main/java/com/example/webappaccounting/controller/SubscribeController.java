package com.example.webappaccounting.controller;

import com.example.webappaccounting.model.Subscribe;
import com.example.webappaccounting.repository.ShiftRepo;
import com.example.webappaccounting.service.SubscribeService;
import com.example.webappaccounting.service.ParseHelper;
import com.example.webappaccounting.service.ShiftServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/subscribe")
public class SubscribeController {
    @Autowired
    private SubscribeService subscribeService;

    @Autowired
    private ShiftServiceImpl service;

    @Autowired
    private ShiftRepo shiftRepo;

    @Autowired
    private ParseHelper parseHelper;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public String subscribeList(@RequestParam(name="person", required=false) String person,
                                Map<String, Object> model) {
        model.put("employees", subscribeService.findAll());
        if (person == null) {
            person = "";
        }
        model.put("select", person);

        HashSet<String> persons = (HashSet<String>) parseHelper.getNameInRangeWithout85(
                LocalDate.now().minusMonths(2),
                LocalDate.now());
        ArrayList<String> names = (ArrayList<String>) persons.stream()
                .sorted()
                .collect(Collectors.toList());
        model.put("persons", names);
        return "employee";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public String addSubscribe(@RequestParam String person, @RequestParam String email, Map<String, Object> model) {
        if (parseHelper.patternMatches(email)) {
            subscribeService.addSubscribe(new Subscribe(person, email));
        } else {
            model.put("message", "Неправильный формат email");
        }


        return "redirect:/subscribe";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}/delete")
    public String deleteSubscribe(@PathVariable("id") Long id) {
        Optional<Subscribe> subscribe = subscribeService.findById(id);
        subscribe.ifPresent(subscribeService::deleteSubscribe);

        return "redirect:/subscribe";
    }
}

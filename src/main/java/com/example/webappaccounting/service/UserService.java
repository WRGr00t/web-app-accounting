package com.example.webappaccounting.service;

import com.example.webappaccounting.model.Role;
import com.example.webappaccounting.model.Subscribe;
import com.example.webappaccounting.model.User;
import com.example.webappaccounting.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SubscribeService subscribeService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }

        return user;
    }

    public boolean addUser(User user) {
        User userFromDB = userRepo.findByUsername(user.getUsername());

        if (userFromDB !=null || user.getUsername().isEmpty() || user.getPassword().isEmpty()) {
            return false;
        }
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmail(user.getEmail());
        userRepo.save(user);

        return true;
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public void saveUser(User user, String username, String email, Map<String, String> form) {
        user.setUsername(username);
        user.setEmail(Objects.requireNonNullElse(email, ""));

        Set<String> roles = Arrays.stream(Role.values())
                        .map(Role::name)
                        .collect(Collectors.toSet());
        user.getRoles().clear();

        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key));
            }
        }
        userRepo.save(user);
    }

    public void updateProfile(User user,
                              String username,
                              String email,
                              String nameForSubscribe,
                              String password) {

        if (!StringUtils.isEmpty(username) && !user.getUsername().equals(username)) {
            user.setUsername(username);
        }
        if (!StringUtils.isEmpty(password)) {
            user.setPassword(passwordEncoder.encode(password));
        }
        String mail = user.getEmail();
        if (email != null) {
            if (email.isEmpty()) {
                mail = email;
            }
        }
        if (nameForSubscribe != null && !mail.isEmpty()) {
            Subscribe newSubscribe = new Subscribe(nameForSubscribe, mail);
            subscribeService.addSubscribe(newSubscribe);
        }
        if (email != null) {
            user.setEmail(email);
        }
        userRepo.save(user);
    }

    public Optional<User> findById(Long id) {
        return userRepo.findById(id);
    }
}

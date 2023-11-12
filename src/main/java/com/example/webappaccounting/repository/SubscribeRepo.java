package com.example.webappaccounting.repository;

import com.example.webappaccounting.model.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscribeRepo extends JpaRepository<Subscribe, Long> {
    List<Subscribe> findAll();

    Subscribe findByUsernameAndEmail(String username, String email);

    Subscribe findByUsername(String name);

    List<Subscribe> findAllMailByUsername(String name);
}

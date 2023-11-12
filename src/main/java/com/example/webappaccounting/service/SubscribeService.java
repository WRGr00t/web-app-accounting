package com.example.webappaccounting.service;

import com.example.webappaccounting.model.Subscribe;
import com.example.webappaccounting.repository.SubscribeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SubscribeService {
    @Autowired
    private SubscribeRepo subscribeRepo;

    public void addSubscribe(Subscribe subscribe) {
        subscribeRepo.save(subscribe);
    }

    public void deleteSubscribe(Subscribe subscribe) {
        subscribeRepo.delete(subscribe);
    }

    public List<Subscribe> findAll() {
        return subscribeRepo.findAll();
    }

    public Subscribe findFirst(String name, String email) {
        return subscribeRepo.findByUsernameAndEmail(name, email);
    }

    public Optional<Subscribe> findById(Long id) {
        return  subscribeRepo.findById(id);
    }

    public Subscribe findByUsername(String name) {
        return subscribeRepo.findByUsername(name);
    }

    public ArrayList<Subscribe> findAllMailByUsername(String name) {
        return (ArrayList<Subscribe>) subscribeRepo.findAllMailByUsername(name);
    }
}

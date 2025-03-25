package com.example.aslapp_backend.sevices;

import com.example.aslapp_backend.models.user;
import com.example.aslapp_backend.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class UserSave {
    @Autowired
    UserRepository userRepository;
    @Transactional
    public user saveUser(user user) {

         return    userRepository.save(user);  // Save the user to the database


        // Save the user to the database
    }

}

package com.example.aslapp_backend.repositories;

import com.example.aslapp_backend.models.ERole;
import com.example.aslapp_backend.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByname(String name);
    boolean existsByname(String name);
}

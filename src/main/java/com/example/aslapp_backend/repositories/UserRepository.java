package com.example.aslapp_backend.repositories;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.aslapp_backend.models.user;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<user, Long> {


    Optional<user> findById(Long aLong);

    Optional<user> findByusername(@NotBlank String username);

    Optional<user> findByEmail(@NotBlank @Size(max = 50) @Email String email);

    Boolean existsByEmail(@NotBlank @Size(max = 50) @Email String email);


}

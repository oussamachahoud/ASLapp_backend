package com.example.aslapp_backend.repositories;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.aslapp_backend.models.user;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<user, Long> {


    @Query("select u from user u left JOIN FETCH u.address where u.id = :id")
    Optional<user> findByIdWithAddresses(@Param("id") Long id);

    @Query("SELECT DISTINCT u FROM user u LEFT JOIN FETCH u.address")
    Page<user> findAllWithAddresses(Pageable pageable);
    Optional<user> findByusername(@NotBlank String username);

    Optional<user> findByEmail(@NotBlank @Size(max = 50) @Email String email);

    Boolean existsByEmail(@NotBlank @Size(max = 50) @Email String email);

    Boolean existsByUsername(String username);

    Optional<user> findById(Long id);

    Page<user> findAll(Pageable pageable);

    void deleteById(Long id);
}
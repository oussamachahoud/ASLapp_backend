package com.example.aslapp_backend.repositories;

import com.example.aslapp_backend.models.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByUsername(String username);

    @Query("select DISTINCT u from User u"+
            " left JOIN FETCH u.address "+
            "left JOIN FETCH u.roles "+
            "where u.id = :id")
    Optional<User> findByIdWithAddresses(@Param("id") Long id);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.address left JOIN FETCH u.roles")
    Page<User> findAllUsers(Pageable pageable);

    Optional<User> findByusername(@NotBlank String username);

    @Query("select DISTINCT u from User u left JOIN FETCH u.roles where u.email = :email")
    Optional<User> findByEmail(@Param("email") @NotBlank @Size(max = 50) @Email String email);

    Boolean existsByEmail(@NotBlank @Size(max = 50) @Email String email);

    Boolean existsByUsername(String username);

    Optional<User> findById(Long id);

    Page<User> findAll(Pageable pageable);

    void deleteById(Long id);
}
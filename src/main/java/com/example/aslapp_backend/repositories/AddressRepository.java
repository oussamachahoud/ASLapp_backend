package com.example.aslapp_backend.repositories;

import com.example.aslapp_backend.models.Address;
import com.example.aslapp_backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    void deleteAllByIdAndUser(Long id, User user);
}

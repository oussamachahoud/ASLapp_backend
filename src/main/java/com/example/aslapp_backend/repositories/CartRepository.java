package com.example.aslapp_backend.repositories;
import com.example.aslapp_backend.models.user;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.aslapp_backend.models.Cart;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {

     List<Cart> findByUser(user user) ;
}

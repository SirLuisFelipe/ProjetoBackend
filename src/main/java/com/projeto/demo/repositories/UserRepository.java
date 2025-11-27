package com.projeto.demo.repositories;

import com.projeto.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(final String email);

    List<User> findByNameContainingIgnoreCase(String name);

}

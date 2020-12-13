package com.buinevich.task4.model.repositories;

import com.buinevich.task4.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    boolean existsByMail(String mail);
    Optional<User> findByMail(String mail);
}

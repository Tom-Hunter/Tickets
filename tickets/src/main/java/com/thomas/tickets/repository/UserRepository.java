package com.thomas.tickets.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thomas.tickets.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByName(String username);
}
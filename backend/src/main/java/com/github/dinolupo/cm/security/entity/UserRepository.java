package com.github.dinolupo.cm.security.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameAndDisabled(String username, Boolean disabled);
    Page<User> findByDisabled(Boolean disabled, Pageable pageable);

}
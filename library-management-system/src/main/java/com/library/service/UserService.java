package com.library.service;

import com.library.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {

    User register(User user);

    Optional<User> findByUsername(String username);

    User updateUser(User user);

    boolean deleteUser(Long id);

    Page<User> findAll(Pageable pageable);

    Optional<User> findById(Long id);
}
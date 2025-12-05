package com.library.service;

import com.library.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User register(User user);

    Optional<User> authenticate(String username, String password);

    boolean checkPassword(User user, String rawPassword);

    void changePassword(User user, String newPassword);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    User updateUser(User user);

    boolean deleteUser(Long id);

    Page<User> findAll(Pageable pageable);

    Optional<User> findById(Long id);

    Page<User> findByUsernameContaining(String keyword, Pageable pageable);

    long countUsers();

    long countActiveUsers();

    List<User> searchUsers(String keyword);

    List<User> findByStatus(Integer status);
}
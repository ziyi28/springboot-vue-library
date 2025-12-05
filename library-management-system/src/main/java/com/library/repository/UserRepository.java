package com.library.repository;

import com.library.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Page<User> findByUsernameContainingIgnoreCase(String keyword, Pageable pageable);

    long countByStatus(Integer status);

    List<User> findByUsernameContainingIgnoreCaseOrRealNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String realName, String email);

    List<User> findByStatus(Integer status);
}
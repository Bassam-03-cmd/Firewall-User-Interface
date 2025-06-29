package com.example.demo.repo;

import com.example.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserEmailAndUserPassword(String email, String password);
    Optional<User> findByUserNameAndUserPassword(String userName, String userPassword);
    boolean existsByUserEmail(String userEmail);
    boolean existsByUserName(String userName);
}

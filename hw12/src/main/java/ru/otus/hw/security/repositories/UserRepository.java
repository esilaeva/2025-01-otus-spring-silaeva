package ru.otus.hw.security.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.security.models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameIgnoreCase(String username);
}
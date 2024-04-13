package org.danmachi_web.repository;

import org.danmachi_web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}

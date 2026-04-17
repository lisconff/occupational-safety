package com.zhituan.backend.repository.user;

import com.zhituan.backend.domain.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);

    List<User> findByUserIdIn(Collection<String> userIds);

    List<User> findByUsernameIn(Collection<String> usernames);
}

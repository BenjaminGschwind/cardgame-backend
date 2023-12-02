package com.pse.cardit.user.repository;

import com.pse.cardit.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUserId(long userId);

    List<User> findUsersByUsername(String username);
}

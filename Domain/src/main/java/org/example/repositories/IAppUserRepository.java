package org.example.repositories;

import org.example.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IAppUserRepository extends JpaRepository<AppUser, Integer> {
    Optional<AppUser> findByUserName (String userName);

    Boolean existsByUserName(String userName);

    Boolean existsByEmail(String email);

    Optional<AppUser> findByEmail (String email);
}

package com.backbase.moviesdigger.repository;

import com.backbase.moviesdigger.auth.service.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User, String> {
    boolean existsByNameIs(String userName);

    Optional<User> findByNameIs(String userName);
}

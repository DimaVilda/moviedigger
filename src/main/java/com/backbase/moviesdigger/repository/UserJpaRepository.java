package com.backbase.moviesdigger.repository;

import com.backbase.moviesdigger.auth.service.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<User, String> {

    User findUserByNameIs(@Param("userName") String userName);
}

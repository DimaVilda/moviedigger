package com.backbase.moviesdigger.repository;

import com.backbase.moviesdigger.auth.service.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, String> {

    @Query("select rt from user_information ui " +
            "join ui.refreshToken rt " +
            "where ui.name = :userName ")
    RefreshToken findExpiredRefreshTokenByUserName(@Param("userName") String userName);
}

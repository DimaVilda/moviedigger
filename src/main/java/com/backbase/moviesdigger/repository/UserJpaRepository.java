package com.backbase.moviesdigger.repository;

import com.backbase.moviesdigger.auth.service.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<User, String> {
    @Query("select case when ui.state = 'LOGGED_IN' then ui.refreshToken.tokenValue else '' end " +
            "from user_information ui " +
            "where ui.name = :userName")
    String findRefreshTokenByUserNameIfLoggedIn(@Param("userName") String userName);

    @Query("select case when ui.state = 'LOGGED_IN' then true else false end " +
            "from user_information ui " +
            "where ui.name = :userName")
    boolean isLoggedInByUserName(@Param("userName") String userName);

    User findByNameIs(@Param("userName") String userName);
}

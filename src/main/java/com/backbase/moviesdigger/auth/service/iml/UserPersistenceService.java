package com.backbase.moviesdigger.auth.service.iml;

import com.backbase.moviesdigger.auth.service.domain.RefreshToken;
import com.backbase.moviesdigger.auth.service.domain.User;
import com.backbase.moviesdigger.auth.service.domain.enums.UserStatesEnum;
import com.backbase.moviesdigger.client.spec.model.LoggedInUserResponse;
import com.backbase.moviesdigger.repository.RefreshTokenJpaRepository;
import com.backbase.moviesdigger.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserPersistenceService {

    private final UserJpaRepository userJpaRepository;
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    public void saveLoggedInUserAndHisToken(String userName,
                                            LoggedInUserResponse refreshTokenToResponse) {
        log.debug("Saving a logged in user {} and his refresh token to db", userName);
        User user = new User();
        user.setName(userName);
        user.setState(UserStatesEnum.LOGGED_IN);
        userJpaRepository.save(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenValue(refreshTokenToResponse.getRefreshToken());
        refreshToken.setExpirationTime(refreshTokenToResponse.getRefreshExpiresIn());
        refreshToken.setCreationTime(Instant.now());
        refreshToken.setUserInformation(user);

        user.setRefreshToken(refreshToken);

       refreshTokenJpaRepository.save(refreshToken);
    }

    public String findRefreshTokenByUserNameIfLoggedIn(String userName) {
        return userJpaRepository.findRefreshTokenByUserNameIfLoggedIn(userName);
    }

    public boolean isLoggedInByUserName(String userName) {
        return userJpaRepository.isLoggedInByUserName(userName);
    }

    public void updateUserStatusToLoggedIn(String username) {
       User loggedOutUser = userJpaRepository.findByNameIs(username);
       loggedOutUser.setState(UserStatesEnum.LOGGED_IN);
       userJpaRepository.save(loggedOutUser);
    }
}

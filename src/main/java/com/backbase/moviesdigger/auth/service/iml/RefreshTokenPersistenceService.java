package com.backbase.moviesdigger.auth.service.iml;

import com.backbase.moviesdigger.auth.service.domain.RefreshToken;
import com.backbase.moviesdigger.repository.RefreshTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenPersistenceService {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    public RefreshToken checkIfRefreshTokenExpired(String userName, int creationTime) {
       RefreshToken refreshToken = refreshTokenJpaRepository.findExpiredRefreshTokenByUserName(userName);
       Instant expirationTimestamp = refreshToken.getCreationTime().plusSeconds(creationTime);
       return Instant.now().isAfter(expirationTimestamp) ? refreshToken : null;
    }

    public void saveNewRefreshTokenOfExistingLoggedInUser(RefreshToken newToken, String refreshTokenValue, int expirationTime) {
        newToken.setTokenValue(refreshTokenValue);
        newToken.setExpirationTime(expirationTime);
        newToken.setCreationTime(Instant.now());
    }
}

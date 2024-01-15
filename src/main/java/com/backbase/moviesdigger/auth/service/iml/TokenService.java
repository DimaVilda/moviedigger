package com.backbase.moviesdigger.auth.service.iml;

import com.backbase.moviesdigger.auth.service.domain.RefreshToken;
import com.backbase.moviesdigger.client.spec.model.LoggedInUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenPersistenceService refreshTokenPersistenceService;

    public void handleRefreshTokenExpiration(String userName, Pair<String, LoggedInUserResponse> refreshTokenToResponsePair) {
        RefreshToken expiredToken = refreshTokenPersistenceService.checkIfRefreshTokenExpired(
                userName,
                refreshTokenToResponsePair.getValue().getRefreshExpiresIn());
        if (expiredToken != null) {
            refreshTokenPersistenceService.saveNewRefreshTokenOfExistingLoggedInUser(
                    expiredToken,
                    refreshTokenToResponsePair.getKey(),
                    refreshTokenToResponsePair.getValue().getRefreshExpiresIn());
        }
    }
}

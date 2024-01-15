package com.backbase.moviesdigger.auth.service.iml;

import com.backbase.moviesdigger.auth.service.domain.RefreshToken;
import com.backbase.moviesdigger.client.spec.model.LoggedInUserResponse;
import com.backbase.moviesdigger.utils.TokenMethodsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.backbase.moviesdigger.utils.consts.JwtClaimsConst.PREFERRED_USERNAME_CLAIM;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenPersistenceService refreshTokenPersistenceService;
    private final TokenMethodsUtil tokenMethodsUtil;

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

    private String getAuthenticatedUserName(String userAccessToken) {
        return tokenMethodsUtil.getUserTokenClaimValue(userAccessToken, PREFERRED_USERNAME_CLAIM);
       // return securityContextUtil.getUserTokenClaim(InternalJwtClaimsSet.SUBJECT_CLAIM, String.class);
    }
}

package com.backbase.moviesdigger.auth.service;

import com.backbase.moviesdigger.client.spec.model.AccessTokenResponse;
import com.backbase.moviesdigger.client.spec.model.LoggedInUserInformation;
import com.backbase.moviesdigger.client.spec.model.LoggedInUserResponse;
import com.backbase.moviesdigger.client.spec.model.LoggedOutUserResponse;

/**
 * Service responsible for login/logout users
 */
public interface UserAuthService {

    /**
     * Login user
     *
     * @param loggedInUserInformation - user name and password values
     *
     * @return - {@link LoggedInUserResponse} - access token, its expiration date, refresh token's expiration date
     */
    LoggedInUserResponse login(LoggedInUserInformation loggedInUserInformation);

    /**
     * End user's session by username from its access token claim
     *
     */
    void endSession();

    /**
     * Get logged-in user's access token
     *
     * @param refreshToken - user's refresh token value
     *
     * @return - {@link AccessTokenResponse} - access token and its expiration date in seconds from keycloak
     */
    AccessTokenResponse getAccessToken(String refreshToken);
}

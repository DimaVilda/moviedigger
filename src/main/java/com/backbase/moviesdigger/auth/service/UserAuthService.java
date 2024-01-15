package com.backbase.moviesdigger.auth.service;

import com.backbase.moviesdigger.client.spec.model.LoggedInUserInformation;
import com.backbase.moviesdigger.client.spec.model.LoggedInUserResponse;

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
     * Login user
     *
     * @param userMame - logged in user name cred
     *
     * @return - {@link LoggedInUserResponse} - access token, its expiration date, refresh token's expiration date
     */
    LoggedInUserResponse getAccessToken(String userMame);
}

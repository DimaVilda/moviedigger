package com.backbase.moviesdigger.auth.service;

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
     * Logout user
     *
     * @return - {@link LoggedOutUserResponse} - user's name cred and his state after successful logout
     */
    LoggedOutUserResponse logout();

    /**
     * Get logged-in user's access token
     *
     * @param userMame - logged-in user's name cred
     *
     * @return - {@link LoggedInUserResponse} - access token, its expiration date, refresh token's expiration date
     */
    LoggedInUserResponse getAccessToken(String userMame);
}

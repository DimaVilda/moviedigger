package com.backbase.moviesdigger.auth.service;

import com.backbase.moviesdigger.client.spec.model.LoggedInUserInformation;
import com.backbase.moviesdigger.client.spec.model.LoggedInUserResponse;

/**
 * Service responsible for login/logout users
 */
public interface UserLoginService {

    /**
     * Login user
     *
     * @param loggedInUserInformation - user name and password values
     *
     * @return - {@link LoggedInUserResponse} - access token, its expiration date, refresh token's expiration date
     */
    LoggedInUserResponse login(LoggedInUserInformation loggedInUserInformation);
}

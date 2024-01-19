package com.backbase.moviesdigger.service;

import com.backbase.moviesdigger.client.spec.model.AccessTokenResponse;
import com.backbase.moviesdigger.client.spec.model.LoggedInUserResponse;
import com.backbase.moviesdigger.client.spec.model.UserInformationRequestBody;

/**
 * Service responsible for interacting with application users
 */
public interface UserService {

    /**
     *
     * @param userInformationRequestBody - user's name and password values to create him in db and keycloak
     */
    void createUser(UserInformationRequestBody userInformationRequestBody);

    /**
     *
     * @param userName - user's name to proceed with removing
     *
     */
    void deleteUser(String userName);
    /**
     * Login user
     *
     * @param loggedInUserInformation - user name and password values
     *
     * @return - {@link LoggedInUserResponse} - access token, its expiration date, refresh token's expiration date
     */
    LoggedInUserResponse login(UserInformationRequestBody loggedInUserInformation);

    /**
     * End user's session by username from its access token claim
     *
     */
    void endSession();

    /**
     * Get logged-in user's access token
     *
     * @param refreshToken - user's refresh token value
     * @param previousAccessToken - user's previous access token to revoke
     *
     * @return - {@link AccessTokenResponse} - access token and its expiration date in seconds from keycloak
     */
    AccessTokenResponse getAccessToken(String refreshToken, String previousAccessToken);
}

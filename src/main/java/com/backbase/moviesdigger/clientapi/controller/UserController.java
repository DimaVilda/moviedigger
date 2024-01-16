package com.backbase.moviesdigger.clientapi.controller;

import com.backbase.moviesdigger.auth.service.UserAuthService;
import com.backbase.moviesdigger.client.spec.api.UserClientApi;
import com.backbase.moviesdigger.client.spec.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController implements UserClientApi {

    private final UserAuthService userAuthService;

    @Override
    public ResponseEntity<AccessTokenResponse> getAccessToken(@RequestBody RefreshTokenRequestBody refreshTokenRequestBody) {
        log.debug("Trying to get an access token for a user");

        return new ResponseEntity<>(userAuthService.getAccessToken(refreshTokenRequestBody.getRefreshToken()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<LoggedInUserResponse> userLogin(@RequestBody LoggedInUserInformation loggedInUserInformation) {
        log.debug("Trying to log in a user {}", loggedInUserInformation.getUserName());

        return new ResponseEntity<>(userAuthService.login(loggedInUserInformation), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> endSession() {
        log.debug("Trying to logout a uswer");

        userAuthService.endSession();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

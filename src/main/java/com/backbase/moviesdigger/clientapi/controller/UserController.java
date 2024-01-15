package com.backbase.moviesdigger.clientapi.controller;

import com.backbase.moviesdigger.auth.service.UserAuthService;
import com.backbase.moviesdigger.client.spec.api.UserClientApi;
import com.backbase.moviesdigger.client.spec.model.LoggedInUserInformation;
import com.backbase.moviesdigger.client.spec.model.LoggedInUserResponse;
import com.backbase.moviesdigger.client.spec.model.UserLoginStatesEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController implements UserClientApi {

    private final UserAuthService userAuthService;

    @Override
    public ResponseEntity<LoggedInUserResponse> getAccessToken(String userMame) {
        log.debug("Trying to get an access token for a user {}", userMame);

        return new ResponseEntity<>(userAuthService.getAccessToken(userMame), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<LoggedInUserResponse> userLogin(LoggedInUserInformation loggedInUserInformation) {
        log.debug("Trying to log in a user {}", loggedInUserInformation.getUserName());

        return new ResponseEntity<>(userAuthService.login(loggedInUserInformation), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<UserLoginStatesEnum> userLogout() {
        return null;
    }
}

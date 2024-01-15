package com.backbase.moviesdigger.clientapi.controller;

import com.backbase.moviesdigger.auth.service.UserLoginService;
import com.backbase.moviesdigger.client.spec.api.UserClientApi;
import com.backbase.moviesdigger.client.spec.model.LoggedInUserInformation;
import com.backbase.moviesdigger.client.spec.model.LoggedInUserResponse;
import com.backbase.moviesdigger.client.spec.model.UserLogoutRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController implements UserClientApi {

    private final UserLoginService userLoginService;

    @Override
    public ResponseEntity<LoggedInUserResponse> getAccessToken() {
        return null;
    }

    @Override
    public ResponseEntity<LoggedInUserResponse> userLogin(LoggedInUserInformation loggedInUserInformation) {
        log.debug("Trying to log in a user {}", loggedInUserInformation.getUserName());

        return new ResponseEntity<>(userLoginService.login(loggedInUserInformation), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> userLogout(UserLogoutRequest userLogoutRequest) {
        return null;
    }
}

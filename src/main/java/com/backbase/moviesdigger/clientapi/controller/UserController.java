package com.backbase.moviesdigger.clientapi.controller;

import com.backbase.moviesdigger.service.UserService;
import com.backbase.moviesdigger.client.spec.api.UserClientApi;
import com.backbase.moviesdigger.client.spec.model.*;
import com.backbase.moviesdigger.utils.validation.sequences.UserCredsSequence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.backbase.moviesdigger.utils.consts.KeycloakConsts.REALM_ADMIN_ROLE;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController implements UserClientApi {

    private final UserService userService;

    @Override
    public ResponseEntity<Void> createUser(@RequestBody
                                               @Validated(UserCredsSequence.class)
                                               UserInformationRequestBody userInformationRequestBody) {
        log.debug("Trying to create a new user {} ", userInformationRequestBody.getUserName());

        userService.createUser(userInformationRequestBody);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    @PreAuthorize("hasRole('" + REALM_ADMIN_ROLE + "')")
    public ResponseEntity<Void> deleteUser(@PathVariable String userName) {
        log.debug("Trying to delete a user {} ", userName);

        userService.deleteUser(userName);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @Override
    public ResponseEntity<AccessTokenResponse> getAccessToken(@RequestBody AccessTokenRequestBody refreshTokenRequestBody) {
        log.debug("Trying to get an access token for a user");

        return new ResponseEntity<>(userService.getAccessToken(
                refreshTokenRequestBody.getRefreshToken(),
                refreshTokenRequestBody.getPreviousAccessToken()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<LoggedInUserResponse> userLogin(@RequestBody
                                                              @Validated(UserCredsSequence.class)
                                                              UserInformationRequestBody userInformationRequestBody) {
        log.debug("Trying to log in a user {}", userInformationRequestBody.getUserName());

        return new ResponseEntity<>(userService.login(userInformationRequestBody), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> endSession() {
        log.debug("Trying to logout a user");

        userService.endSession();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

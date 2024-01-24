package com.backbase.moviesdigger.clientapi.controller;

import com.backbase.moviesdigger.client.spec.model.AccessTokenRequestBody;
import com.backbase.moviesdigger.client.spec.model.AccessTokenResponse;
import com.backbase.moviesdigger.client.spec.model.UserInformationRequestBody;
import com.backbase.moviesdigger.exceptions.ConflictException;
import com.backbase.moviesdigger.exceptions.NotFoundException;
import com.backbase.moviesdigger.exceptions.UnauthorizedException;
import com.backbase.moviesdigger.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@WithMockUser
class UserControllerMvcTest {

    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private final static String URL = "/client-api/v1/users";

    @Test
    void shouldThrowBadRequestOnCreateUserWhenPasswordCredInvalid() throws Exception {
        UserInformationRequestBody request = new UserInformationRequestBody();
        request.setUserName("user");
        request.setPassword("^%&");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowBadRequestOnCreateUserWhenUserNameCredInvalid() throws Exception {
        UserInformationRequestBody request = new UserInformationRequestBody();
        request.setUserName("^%&");
        request.setPassword("123");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowNotFoundOnDeleteUser() throws Exception {
        doThrow(new NotFoundException("Not found")).when(userService).deleteUser(any());

        mockMvc.perform(delete(URL + "/non-exist-user")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnSuccessOnDeleteUser() throws Exception {
        mockMvc.perform(delete(URL + "/user")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnSuccessOnGetAccessToken() throws Exception {
        AccessTokenRequestBody request = new AccessTokenRequestBody();
        request.setPreviousAccessToken("prev-token");
        request.setRefreshToken("refresh-token");

        when(userService
                .getAccessToken(any(), any())).thenReturn(new AccessTokenResponse());

        mockMvc.perform(post(URL + "/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowUnauthorizedExceptionOnGetAccessToken() throws Exception {
        AccessTokenRequestBody request = new AccessTokenRequestBody();
        request.setPreviousAccessToken("prev-token");
        request.setRefreshToken("refresh-token");

        when(userService
                .getAccessToken(any(), any())).thenThrow(new UnauthorizedException("User is not logged in"));

        mockMvc.perform(post(URL + "/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldThrowBadRequestOnLoginUserWhenPasswordCredInvalid() throws Exception {
        UserInformationRequestBody request = new UserInformationRequestBody();
        request.setUserName("user");
        request.setPassword("^%&");

        mockMvc.perform(post(URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowBadRequestOnLoginUserWhenUserNameCredInvalid() throws Exception {
        UserInformationRequestBody request = new UserInformationRequestBody();
        request.setUserName("^%&");
        request.setPassword("123");

        mockMvc.perform(post(URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowNotFoundOnLoginUserWhenUserNameCredInvalid() throws Exception {
        UserInformationRequestBody request = new UserInformationRequestBody();
        request.setUserName("user");
        request.setPassword("123");

        when(userService
                .login(any())).thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(post(URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnSuccessOnUserEndSession() throws Exception {
        mockMvc.perform(get(URL + "/login/endsession"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowConflictOnUserEndSession() throws Exception {
        doThrow(new ConflictException("Conflict")).when(userService).endSession();
        mockMvc.perform(get(URL + "/login/endsession"))
                .andExpect(status().isConflict());
    }
}

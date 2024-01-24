package com.backbase.moviesdigger.clientapi.controller;

import com.backbase.moviesdigger.client.spec.model.*;
import com.backbase.moviesdigger.exceptions.NotFoundException;
import com.backbase.moviesdigger.service.MoviesDiggerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MoviesDiggerController.class)
@WithMockUser
class MoviesDiggerControllerMvcTest {

    @MockBean
    private MoviesDiggerService moviesDiggerService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private final static String URL = "/client-api/v1/movies/";

    @Test
    void shouldReturnBadRequestOnGetMoviesWhenUUIDisNotValid() throws Exception {
        String movieName = "movieName";
        when(moviesDiggerService
                .getMovies(any(), any())).thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(get(URL + movieName))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnSuccessWhenGetMovies() throws Exception {
        when(moviesDiggerService
                .getMovies(any(), any())).thenReturn(List.of(new MovieResponseBodyItem()));

        mockMvc.perform(get(URL + "movieName"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnSuccessWhenGetTopRatedMovies() throws Exception {
        when(moviesDiggerService
                .getTopRatedMovies(any(), any(), any())).thenReturn(List.of(new TopRatedMovieResponseBodyItem()));

        mockMvc.perform(get(URL + "top-rated"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnSuccessWhenGetBestPictureMovies() throws Exception {
        when(moviesDiggerService
                .getWinner(any(), any())).thenReturn(List.of(new MovieWinnerResponseBodyItem()));

        mockMvc.perform(get(URL + "movieName" + "/iswon"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundOnGetBestPictureMovieName() throws Exception {
        when(moviesDiggerService
                .getWinner(any(), any())).thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(get(URL + "non-exist-name" + "/iswon"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestOnGetBestPictureMovieName() throws Exception {
        mockMvc.perform(get(URL + "^" + "/iswon")
                        .queryParam("year", "non-valid-year"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnSuccessWhenProvideMovieRating() throws Exception {
        MovieRatingRequestBody request = new MovieRatingRequestBody();
        request.setRating(2);
        request.setMovieId("466b8c0e-238e-46f3-9dba-dae01439bc26");

        when(moviesDiggerService
                .provideMovieRating(any())).thenReturn(new MovieRatingResponseBody());

        mockMvc.perform(post(URL + "rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldThrowBadRequestWhenProvideMovieRatingInvalidRequest() throws Exception {
        MovieRatingRequestBody request = new MovieRatingRequestBody();
        request.setRating(2);
        request.setMovieId("invalid-movie-uuid");

        mockMvc.perform(post(URL + "rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowNotFoundWhenProvideMovieRating() throws Exception {
        MovieRatingRequestBody request = new MovieRatingRequestBody();
        request.setRating(2);
        request.setMovieId("466b8c0e-238e-46f3-9dba-dae01439bc26");

        when(moviesDiggerService
                .provideMovieRating(any())).thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(post(URL + "rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnSuccessOnDeleteMovieRating() throws Exception {
        mockMvc.perform(delete(URL + "rating" + "/466b8c0e-238e-46f3-9dba-dae01439bc26")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowBadRequestOnDeleteMovieRatingInvalidUUID() throws Exception {
        mockMvc.perform(delete(URL + "rating" + "/invalid-uuid-value")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}

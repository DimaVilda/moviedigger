package com.backbase.moviesdigger.service.impl;

import com.backbase.moviesdigger.exceptions.NotFoundException;
import com.backbase.moviesdigger.repository.MovieJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MoviePersistenceServiceTest {
    @Mock
    private MovieJpaRepository movieJpaRepository;
    @InjectMocks
    private MoviePersistenceService moviePersistenceService;

    @Test
    void shouldThrowNotFoundOnGetMovieByRatingId() {
        String testRatingId = "testRatingId";
        when(movieJpaRepository.findByRatingId(testRatingId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> moviePersistenceService.getMovieByRatingId(testRatingId));
        assertThat(exception.getMessage(), is("Rating " + testRatingId + " was not found"));
    }

    @Test
    void shouldThrowNotFoundOnGetMovieById() {
        String testMovieId = "testMovieId";
        when(movieJpaRepository.findById(testMovieId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> moviePersistenceService.getMovieById(testMovieId));
        assertThat(exception.getMessage(), is("Movie " + testMovieId + " was not found"));
    }
}

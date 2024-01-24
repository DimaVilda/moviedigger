package com.backbase.moviesdigger.service.impl;

import com.backbase.moviesdigger.exceptions.GeneralException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class OMDBServiceTest {
    @InjectMocks
    private OMDBService omdbService;

    @Test
    void shouldThrowGeneralExceptionInCaseSomeFailsWithOMDB() {
        String movieName = "Test Movie";
        String movieField = "Title";
        Map<String, Object> fakeResponse = Map.of("Title", "Test Movie");

        GeneralException exception = assertThrows(GeneralException.class, () ->
                omdbService.getMovieFieldByTitle(movieName, movieField, String.class));
        assertThat(exception.getMessage(), is("An unexpected condition was encountered when OMDB was requested, " +
                "reason is HTTP URL must not be null"));
    }
}

package com.backbase.moviesdigger.service.impl;

import com.backbase.moviesdigger.TestUtils;
import com.backbase.moviesdigger.client.spec.model.MovieResponseBodyItem;
import com.backbase.moviesdigger.client.spec.model.MovieWinnerResponseBodyItem;
import com.backbase.moviesdigger.client.spec.model.TopRatedMovieResponseBodyItem;
import com.backbase.moviesdigger.domain.Movie;
import com.backbase.moviesdigger.mappers.MovieResponseBodyItemMapper;
import com.backbase.moviesdigger.mappers.MovieWinnerResponseBodyItemMapper;
import com.backbase.moviesdigger.mappers.TopRatedMovieResponseBodyItemMapper;
import com.backbase.moviesdigger.service.MovieProviderService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SyncServiceTest {
    @Mock
    private MoviePersistenceService moviePersistenceService;
    @Mock
    private MovieProviderService movieProviderService;
    @Mock
    private MovieResponseBodyItemMapper movieResponseBodyItemMapper;
    @Mock
    private TopRatedMovieResponseBodyItemMapper topRatedMovieResponseBodyItemMapper;
    @Mock
    private MovieWinnerResponseBodyItemMapper movieWinnerResponseBodyItemMapper;
    @InjectMocks
    private SyncService syncService;

    private static final String movieName = "testMovieName";
    private static final Integer year = 2021;


    @Test
    void shouldSuccessfullyGetWinnerFromDb() {
        Movie movie1 = TestUtils.createMovieFixture(movieName, 1, year, null, null);
        Movie movie2 = TestUtils.createMovieFixture(movieName, 0, year, null, null);
        List<Movie> movies = List.of(movie1, movie2);
        when(moviePersistenceService.getMoviesByNameAndOptionalYear(movieName, year)).thenReturn(movies);

        MovieWinnerResponseBodyItem mappedResponse = new MovieWinnerResponseBodyItem();
        mappedResponse.setName(movie1.getName());
        mappedResponse.setYear(movie1.getReleaseYear());
        when(movieWinnerResponseBodyItemMapper.toMovieWinnerResponseBodyItemList(Collections.singletonList(movie1)))
                .thenReturn(List.of(mappedResponse));

        List<MovieWinnerResponseBodyItem> winnerList = syncService.getWinner(movieName, year);
        assertFalse(winnerList.isEmpty());

        MovieWinnerResponseBodyItem winnerItem = winnerList.get(0);
        verifyNoInteractions(movieProviderService);
        assertThat(winnerItem.getName(), is(movie1.getName()));
        assertThat(winnerItem.getYear(), is(movie1.getReleaseYear()));
    }

    @Test
    void shouldSuccessfullyGetWinnerFromOMDB() {
        Movie movieFromOmdb = TestUtils.createMovieFixture(movieName, 1, year, null, null);

        when(moviePersistenceService.getMoviesByNameAndOptionalYear(movieName, year)).thenReturn(Collections.emptyList());
        when(moviePersistenceService.saveMovie(any())).thenReturn(movieFromOmdb);
        MovieWinnerResponseBodyItem mappedResponse = new MovieWinnerResponseBodyItem();
        mappedResponse.setName(movieFromOmdb.getName());
        mappedResponse.setYear(movieFromOmdb.getReleaseYear());
        when(movieWinnerResponseBodyItemMapper.toMovieWinnerResponseBodyItemModel(movieFromOmdb)).thenReturn(mappedResponse);

        List<MovieWinnerResponseBodyItem> winnerList = syncService.getWinner(movieName, year);
        assertFalse(winnerList.isEmpty());

        MovieWinnerResponseBodyItem winnerItem = winnerList.get(0);
        verify(movieProviderService).getMovieByTitleAndYearOptional(movieName, year);
        assertThat(winnerItem.getName(), is(movieFromOmdb.getName()));
        assertThat(winnerItem.getYear(), is(movieFromOmdb.getReleaseYear()));
    }

    @Test
    void shouldSuccessfullyGetMoviesWithOfficeBoxFromDb() {
        Movie movie1 = TestUtils.createMovieFixture(movieName, 1, year, null, BigDecimal.valueOf(1000));
        List<Movie> movies = List.of(movie1);
        when(moviePersistenceService.getMoviesByNameAndOptionalYear(movieName, year)).thenReturn(movies);

        MovieResponseBodyItem mappedResponse = new MovieResponseBodyItem();
        mappedResponse.setName(movieName);
        mappedResponse.setYear(year);
        when(movieResponseBodyItemMapper.toMovieResponseBodyItemList(movies))
                .thenReturn(List.of(mappedResponse));

        List<MovieResponseBodyItem> moviesList = syncService.getMovies(movieName, year);
        assertFalse(moviesList.isEmpty());

        MovieResponseBodyItem movieItem = moviesList.get(0);
        verifyNoInteractions(movieProviderService);
        assertThat(movieItem.getName(), is(movieName));
        assertThat(movieItem.getYear(), is(year));
    }

    @Test
    void shouldSuccessfullyGetMoviesFromOMDB() {
        Movie movieFromOmdb = TestUtils.createMovieFixture(movieName, 1, year, null, null);

        when(moviePersistenceService.getMoviesByNameAndOptionalYear(movieName, year)).thenReturn(Collections.emptyList());
        when(moviePersistenceService.saveMovie(any())).thenReturn(movieFromOmdb);

        MovieResponseBodyItem mappedResponse = new MovieResponseBodyItem();
        mappedResponse.setName(movieName);
        mappedResponse.setYear(year);
        when(movieResponseBodyItemMapper.toMovieResponseBodyItemModel(movieFromOmdb))
                .thenReturn(mappedResponse);

        List<MovieResponseBodyItem> moviesList = syncService.getMovies(movieName, year);
        assertFalse(moviesList.isEmpty());

        MovieResponseBodyItem movieItem = moviesList.get(0);
        verify(movieProviderService).getMovieByTitleAndYearOptional(movieName, year);
        assertThat(movieItem.getName(), is(movieName));
        assertThat(movieItem.getYear(), is(year));
    }

    @Test
    void shouldSuccessfullyGetTopRatedMoviesDESC() {
        Sort.Direction sortDirection = Sort.Direction.DESC;
        Movie movie1 = TestUtils.createMovieFixture("movieName1", 1, 2010, null, BigDecimal.valueOf(100));
        Movie movie2 = TestUtils.createMovieFixture("movieName2", 0, 2010, null, BigDecimal.valueOf(200));
        List<Movie> movies = List.of(movie1, movie2);
        when(moviePersistenceService.getTopRatedMoviesByUserRating(any(), any())).thenReturn(movies);

        TopRatedMovieResponseBodyItem mappedMovie1 = new TopRatedMovieResponseBodyItem();
        mappedMovie1.setName(movie2.getName()); //should be 2nd movie on 1st place since we sort DESC by box office
        TopRatedMovieResponseBodyItem mappedMovie2 = new TopRatedMovieResponseBodyItem();
        mappedMovie2.setName(movie1.getName());
        when(topRatedMovieResponseBodyItemMapper.toTopRatedMovieResponseBodyItemList(List.of(movie2, movie1))) //cause it was sorted DESC by box office
                .thenReturn(List.of(mappedMovie1, mappedMovie2));

        List<TopRatedMovieResponseBodyItem> topRatedMovies = syncService.getTopRatedMovies(1, 10, sortDirection);
        assertFalse(topRatedMovies.isEmpty());
        assertThat(topRatedMovies.size(), is(2));
        assertThat(topRatedMovies.get(0).getName(), is(movie2.getName()));
        assertThat(topRatedMovies.get(1).getName(), is(movie1.getName()));
    }
}
